package com.example.pokersolverGTO.trainer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokersolverGTO.data.AppDatabase
import com.example.pokersolverGTO.model.Card
import com.example.pokersolverGTO.model.Deck
import com.example.pokersolverGTO.trainer.engine.GtoSolver
import com.example.pokersolverGTO.trainer.models.*
import com.example.pokersolverGTO.trainer.repository.TrainerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PokerTrainerViewModel(app: Application) : AndroidViewModel(app) {

    private val database = AppDatabase.get(app)
    private val repository = TrainerRepository(database)
    private val gtoSolver = GtoSolver()

    // Current training state
    private val _currentScenario = MutableStateFlow<HandScenario?>(null)
    val currentScenario: StateFlow<HandScenario?> = _currentScenario.asStateFlow()

    private val _trainingMode = MutableStateFlow(TrainingMode.PREFLOP)
    val trainingMode: StateFlow<TrainingMode> = _trainingMode.asStateFlow()

    private val _lastRecommendation = MutableStateFlow<GTORecommendation?>(null)
    val lastRecommendation: StateFlow<GTORecommendation?> = _lastRecommendation.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _decisionStartTime = MutableStateFlow(0L)

    // Player stats
    val playerStats: StateFlow<PlayerStats> = repository.getPlayerStats()
        .stateIn(viewModelScope, SharingStarted.Eagerly, PlayerStats())

    val recentHands: StateFlow<List<TrainingHandResult>> = repository.getRecentHands(20)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Quiz mode
    private val _quizTimeRemaining = MutableStateFlow(30)
    val quizTimeRemaining: StateFlow<Int> = _quizTimeRemaining.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    init {
        // Initialize stats if needed
        viewModelScope.launch {
            repository.getPlayerStatsOnce()
        }
    }

    fun setTrainingMode(mode: TrainingMode) {
        _trainingMode.value = mode
        _lastRecommendation.value = null
        generateNewScenario()
    }

    fun generateNewScenario() {
        viewModelScope.launch {
            _isProcessing.value = true
            _lastRecommendation.value = null
            _decisionStartTime.value = System.currentTimeMillis()

            val scenario = when (_trainingMode.value) {
                TrainingMode.PREFLOP -> generatePreflopScenario()
                TrainingMode.POSTFLOP -> generatePostflopScenario()
                TrainingMode.QUIZ -> generateQuizScenario()
                TrainingMode.SANDBOX -> generateSandboxScenario()
            }

            _currentScenario.value = scenario
            _isProcessing.value = false
        }
    }

    private fun generatePreflopScenario(): HandScenario {
        val deck = Deck().apply { shuffle() }
        val hand = PokerHand(deck.deal(1)[0], deck.deal(1)[0])
        val position = PokerPosition.values().random()

        // 30% chance of facing a raise
        val facingBet = if (kotlin.random.Random.nextFloat() < 0.3f) {
            (2..5).random() * 10 // 20-50 big blinds
        } else 0

        return HandScenario(
            heroHand = hand,
            position = position,
            board = Board(),
            potSize = 15 + facingBet,
            stackSize = 1000,
            facingBet = facingBet
        )
    }

    private fun generatePostflopScenario(): HandScenario {
        val deck = Deck().apply { shuffle() }
        val heroCards = deck.deal(2)
        val hand = PokerHand(heroCards[0], heroCards[1])
        val position = PokerPosition.values().random()

        // Generate flop
        val flop = deck.deal(3)

        // 50% chance of turn, 25% chance of river
        val turn = if (kotlin.random.Random.nextFloat() < 0.5f) deck.deal(1)[0] else null
        val river = if (turn != null && kotlin.random.Random.nextFloat() < 0.5f) deck.deal(1)[0] else null

        val board = Board(flop, turn, river)

        // 60% chance of facing a bet
        val facingBet = if (kotlin.random.Random.nextFloat() < 0.6f) {
            (30..100).random()
        } else 0

        return HandScenario(
            heroHand = hand,
            position = position,
            board = board,
            potSize = 150 + facingBet,
            stackSize = 800,
            facingBet = facingBet
        )
    }

    private fun generateQuizScenario(): HandScenario {
        // Mix of preflop and postflop
        return if (kotlin.random.Random.nextBoolean()) {
            generatePreflopScenario()
        } else {
            generatePostflopScenario()
        }
    }

    private fun generateSandboxScenario(): HandScenario {
        // Similar to postflop but with more control
        return generatePostflopScenario()
    }

    fun submitAction(action: PokerAction, amount: Int = 0) {
        viewModelScope.launch {
            val scenario = _currentScenario.value ?: return@launch
            _isProcessing.value = true

            val playerDecision = ActionDecision(action, amount)
            val recommendation = gtoSolver.analyzeDecision(scenario, playerDecision)

            _lastRecommendation.value = recommendation

            // Calculate time taken
            val timeTaken = System.currentTimeMillis() - _decisionStartTime.value

            // Calculate XP
            val xpEarned = calculateXP(recommendation, timeTaken)

            // Save result
            val result = TrainingHandResult(
                mode = _trainingMode.value,
                heroHandNotation = scenario.heroHand.toNotation(),
                position = scenario.position,
                street = scenario.board.street,
                playerAction = action,
                optimalAction = recommendation.optimalAction,
                evLoss = recommendation.evDifference,
                isCorrect = action == recommendation.optimalAction,
                potSize = scenario.potSize,
                xpEarned = xpEarned,
                timeToDecide = timeTaken
            )

            repository.saveHandResult(result)

            // Update quiz score if in quiz mode
            if (_trainingMode.value == TrainingMode.QUIZ && result.isCorrect) {
                _quizScore.value += xpEarned
            }

            _isProcessing.value = false
        }
    }

    private fun calculateXP(recommendation: GTORecommendation, timeTaken: Long): Int {
        val baseXP = 10

        // Bonus for correct decision
        val correctBonus = if (recommendation.evDifference <= 0.01f) 5 else 0

        // Time bonus (faster decisions get more XP)
        val timeBonus = when {
            timeTaken < 3000 -> 5 // Under 3 seconds
            timeTaken < 5000 -> 3 // Under 5 seconds
            timeTaken < 10000 -> 1 // Under 10 seconds
            else -> 0
        }

        // EV bonus (smaller mistakes get partial credit)
        val evBonus = when {
            recommendation.evDifference < -0.5f -> -2 // Really bad
            recommendation.evDifference < 0f -> 0 // Small mistake
            recommendation.evDifference < 0.1f -> 3 // Very close
            else -> 5 // Optimal
        }

        return maxOf(1, baseXP + correctBonus + timeBonus + evBonus)
    }

    fun nextHand() {
        generateNewScenario()
    }

    fun startQuizMode(durationSeconds: Int = 60) {
        _quizScore.value = 0
        _quizTimeRemaining.value = durationSeconds
        setTrainingMode(TrainingMode.QUIZ)

        // Start countdown
        viewModelScope.launch {
            while (_quizTimeRemaining.value > 0) {
                kotlinx.coroutines.delay(1000)
                _quizTimeRemaining.value -= 1
            }
        }
    }

    fun getSessionSummary() = viewModelScope.launch {
        repository.getSessionSummary()
    }

    fun resetProgress() = viewModelScope.launch {
        repository.resetProgress()
    }
}
