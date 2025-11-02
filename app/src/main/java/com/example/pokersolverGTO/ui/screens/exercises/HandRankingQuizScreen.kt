package com.example.pokersolverGTO.ui.screens.exercises

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokersolverGTO.engine.HandEvaluator
import com.example.pokersolverGTO.model.Card
import com.example.pokersolverGTO.ui.components.ActionButton
import com.example.pokersolverGTO.ui.components.CommunityCards
import com.example.pokersolverGTO.ui.components.HoleCards
import com.example.pokersolverGTO.ui.components.StatsCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class HandRankingState(
    val hand1: List<Card> = emptyList(),
    val hand2: List<Card> = emptyList(),
    val board: List<Card> = emptyList(),
    val result: HandRankingResult? = null,
    val handsPlayed: Int = 0,
    val correctAnswers: Int = 0
)

data class HandRankingResult(
    val isCorrect: Boolean,
    val explanation: String,
    val hand1Rank: String,
    val hand2Rank: String
)

class HandRankingViewModel : ViewModel() {
    private val _state = MutableStateFlow(HandRankingState())
    val state: StateFlow<HandRankingState> = _state

    init {
        generateNewScenario()
    }

    fun generateNewScenario() {
        val deck = com.example.pokersolverGTO.model.Deck().apply { shuffle() }
        val board = deck.deal(5)
        val hand1 = deck.deal(2)
        val hand2 = deck.deal(2)
        _state.value = HandRankingState(hand1 = hand1, hand2 = hand2, board = board)
    }

    fun checkAnswer(answer: String) {
        val current = _state.value
        val eval1 = HandEvaluator.evaluateHand(current.hand1 + current.board)
        val eval2 = HandEvaluator.evaluateHand(current.hand2 + current.board)
        val correctAnswer = when {
            eval1 > eval2 -> "hand1"
            eval2 > eval1 -> "hand2"
            else -> "tie"
        }
        val isCorrect = answer == correctAnswer
        val explanation = when (correctAnswer) {
            "hand1" -> "Hand 1 wins with ${eval1.description}"
            "hand2" -> "Hand 2 wins with ${eval2.description}"
            else -> "It's a tie! Both hands have ${eval1.description}"
        }
        _state.value = current.copy(
            result = HandRankingResult(isCorrect, explanation, eval1.description, eval2.description),
            handsPlayed = current.handsPlayed + 1,
            correctAnswers = if (isCorrect) current.correctAnswers + 1 else current.correctAnswers
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandRankingQuizScreen(
    onBackPressed: () -> Unit,
    viewModel: HandRankingViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Hand Ranking Quiz") }, navigationIcon = { IconButton(onClick = onBackPressed) { Text("←", fontSize = 24.sp) } }) },
        containerColor = Color(0xFF1A1A2E)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatsCard(title = "Hands", value = state.handsPlayed.toString())
                StatsCard(title = "Accuracy", value = if (state.handsPlayed > 0) "${(state.correctAnswers * 100 / state.handsPlayed)}%" else "0%")
            }

            Text(text = "Which hand wins?", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)

            // Community cards
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E))) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Board", fontSize = 16.sp, color = Color.Gray)
                    CommunityCards(cards = state.board)
                }
            }

            // Hand 1
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF2C3E50)), border = BorderStroke(2.dp, Color(0xFF3498DB))) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Hand 1", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3498DB))
                    HoleCards(cards = state.hand1)
                    if (state.result != null) Text(text = state.result!!.hand1Rank, fontSize = 14.sp, color = Color(0xFFFFD700))
                }
            }

            Text(text = "VS", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))

            // Hand 2
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF2C3E50)), border = BorderStroke(2.dp, Color(0xFFE74C3C))) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Hand 2", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE74C3C))
                    HoleCards(cards = state.hand2)
                    if (state.result != null) Text(text = state.result!!.hand2Rank, fontSize = 14.sp, color = Color(0xFFFFD700))
                }
            }

            // Answer buttons or result
            if (state.result == null) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionButton(text = "Hand 1 Wins", onClick = { viewModel.checkAnswer("hand1") }, color = Color(0xFF3498DB), modifier = Modifier.weight(1f))
                        ActionButton(text = "Hand 2 Wins", onClick = { viewModel.checkAnswer("hand2") }, color = Color(0xFFE74C3C), modifier = Modifier.weight(1f))
                    }
                    ActionButton(text = "Tie", onClick = { viewModel.checkAnswer("tie") }, color = Color(0xFF95A5A6), modifier = Modifier.fillMaxWidth())
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (state.result!!.isCorrect) Color(0xFF27AE60) else Color(0xFFE74C3C))) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = if (state.result!!.isCorrect) "✓ Correct!" else "✗ Incorrect", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = state.result!!.explanation, fontSize = 16.sp, color = Color.White)
                    }
                }
                Button(onClick = { viewModel.generateNewScenario() }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB))) {
                    Text("Next Question", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
