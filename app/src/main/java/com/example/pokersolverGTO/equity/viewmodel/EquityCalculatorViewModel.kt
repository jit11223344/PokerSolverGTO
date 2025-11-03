package com.example.pokersolverGTO.equity.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokersolverGTO.equity.logic.EquityCalculator
import com.example.pokersolverGTO.equity.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "EquityCalcVM"

data class EquityCalculatorState(
    val selectedCards: List<Card> = emptyList(),
    val player1Cards: List<Card> = emptyList(),
    val player2Cards: List<Card> = emptyList(),
    val flopCards: List<Card> = emptyList(),
    val turnCard: Card? = null,
    val riverCard: Card? = null,
    val player1WinPercent: Float = 0f,
    val player2WinPercent: Float = 0f,
    val tiePercent: Float = 0f,
    val isCalculating: Boolean = false,
    val calculationDone: Boolean = false,
    val currentSelection: CardSelection = CardSelection.PLAYER1_CARD1,
    val errorMessage: String? = null
)

enum class CardSelection {
    PLAYER1_CARD1,
    PLAYER1_CARD2,
    PLAYER2_CARD1,
    PLAYER2_CARD2,
    FLOP_CARD1,
    FLOP_CARD2,
    FLOP_CARD3,
    TURN,
    RIVER
}

class EquityCalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(EquityCalculatorState())
    val state: StateFlow<EquityCalculatorState> = _state.asStateFlow()

    private val equityCalculator = EquityCalculator()

    fun selectCard(rank: Rank, suit: Suit) {
        val card = Card(rank, suit)

        if (isCardUsed(card)) {
            _state.value = _state.value.copy(errorMessage = "Card already selected")
            return
        }

        val currentState = _state.value
        val newState = when (currentState.currentSelection) {
            CardSelection.PLAYER1_CARD1 -> currentState.copy(
                player1Cards = listOf(card),
                currentSelection = CardSelection.PLAYER1_CARD2,
                errorMessage = null,
                calculationDone = false
            )
            CardSelection.PLAYER1_CARD2 -> currentState.copy(
                player1Cards = currentState.player1Cards + card,
                currentSelection = CardSelection.PLAYER2_CARD1,
                errorMessage = null,
                calculationDone = false
            )
            CardSelection.PLAYER2_CARD1 -> currentState.copy(
                player2Cards = listOf(card),
                currentSelection = CardSelection.PLAYER2_CARD2,
                errorMessage = null,
                calculationDone = false
            )
            CardSelection.PLAYER2_CARD2 -> currentState.copy(
                player2Cards = currentState.player2Cards + card,
                currentSelection = CardSelection.FLOP_CARD1,
                errorMessage = null,
                calculationDone = false
            )
            CardSelection.FLOP_CARD1 -> currentState.copy(
                flopCards = listOf(card),
                currentSelection = CardSelection.FLOP_CARD2,
                errorMessage = null,
                calculationDone = false
            )
            CardSelection.FLOP_CARD2 -> currentState.copy(
                flopCards = currentState.flopCards + card,
                currentSelection = CardSelection.FLOP_CARD3,
                errorMessage = null,
                calculationDone = false
            )
            CardSelection.FLOP_CARD3 -> currentState.copy(
                flopCards = currentState.flopCards + card,
                currentSelection = CardSelection.TURN,
                errorMessage = null,
                calculationDone = false
            )
            CardSelection.TURN -> currentState.copy(
                turnCard = card,
                currentSelection = CardSelection.RIVER,
                errorMessage = null,
                calculationDone = false
            )
            CardSelection.RIVER -> currentState.copy(
                riverCard = card,
                errorMessage = null,
                calculationDone = false
            )
        }

        _state.value = newState
    }

    fun setCurrentSelection(selection: CardSelection) {
        _state.value = _state.value.copy(currentSelection = selection)
    }

    fun clearLastCard() {
        val currentState = _state.value
        val newState = when (currentState.currentSelection) {
            CardSelection.PLAYER1_CARD1 -> currentState
            CardSelection.PLAYER1_CARD2 -> currentState.copy(
                player1Cards = emptyList(),
                currentSelection = CardSelection.PLAYER1_CARD1,
                calculationDone = false
            )
            CardSelection.PLAYER2_CARD1 -> currentState.copy(
                player1Cards = currentState.player1Cards.dropLast(1),
                currentSelection = CardSelection.PLAYER1_CARD2,
                calculationDone = false
            )
            CardSelection.PLAYER2_CARD2 -> currentState.copy(
                player2Cards = emptyList(),
                currentSelection = CardSelection.PLAYER2_CARD1,
                calculationDone = false
            )
            CardSelection.FLOP_CARD1 -> currentState.copy(
                player2Cards = currentState.player2Cards.dropLast(1),
                currentSelection = CardSelection.PLAYER2_CARD2,
                calculationDone = false
            )
            CardSelection.FLOP_CARD2 -> currentState.copy(
                flopCards = emptyList(),
                currentSelection = CardSelection.FLOP_CARD1,
                calculationDone = false
            )
            CardSelection.FLOP_CARD3 -> currentState.copy(
                flopCards = currentState.flopCards.dropLast(1),
                currentSelection = CardSelection.FLOP_CARD2,
                calculationDone = false
            )
            CardSelection.TURN -> currentState.copy(
                flopCards = currentState.flopCards.dropLast(1),
                currentSelection = CardSelection.FLOP_CARD3,
                calculationDone = false
            )
            CardSelection.RIVER -> if (currentState.turnCard != null) {
                currentState.copy(turnCard = null, currentSelection = CardSelection.TURN, calculationDone = false)
            } else {
                currentState.copy(
                    flopCards = currentState.flopCards.dropLast(1),
                    currentSelection = CardSelection.FLOP_CARD3,
                    calculationDone = false
                )
            }
        }

        _state.value = newState.copy(player1WinPercent = 0f, player2WinPercent = 0f, tiePercent = 0f)
    }

    fun clearAll() {
        _state.value = EquityCalculatorState()
    }

    fun calculateEquity() {
        val snapshot = _state.value

        if (snapshot.player1Cards.size < 2) {
            _state.value = snapshot.copy(errorMessage = "Player 1 needs 2 cards")
            return
        }
        if (snapshot.player2Cards.size < 2) {
            _state.value = snapshot.copy(errorMessage = "Player 2 needs 2 cards")
            return
        }

        // mark calculating and clear previous results
        _state.value = _state.value.copy(isCalculating = true, calculationDone = false, errorMessage = null)

        viewModelScope.launch {
            // Capture state at start of calculation to use as base for logging, but we'll always update from latest state
            Log.d(TAG, "Calculation started")

            try {
                // Work with the latest snapshot inside coroutine to avoid stale values
                val current = _state.value
                val communityCards = current.flopCards + listOfNotNull(current.turnCard, current.riverCard)

                // Quick deterministic evaluation if board is complete (5 community cards): no Monte Carlo needed
                val fastResult = if (communityCards.size == 5) {
                    try {
                        equityCalculator.evaluateFinalHands(
                            current.player1Cards,
                            current.player2Cards,
                            communityCards
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Fast evaluation failed: ${e.message}")
                        null
                    }
                } else null

                val iterations = 2000 // lower iterations for responsiveness (1-2s)

                val result = fastResult ?: withContext(Dispatchers.Default) {
                    Log.d(TAG, "Running Monte Carlo with $iterations iterations")
                    equityCalculator.calculateEquity(
                        player1Cards = current.player1Cards,
                        player2Cards = current.player2Cards,
                        communityCards = communityCards,
                        iterations = iterations
                    )
                }

                // Update state using latest state as base to avoid stomping concurrent UI changes
                _state.value = _state.value.copy(
                    player1WinPercent = result.player1WinPercent,
                    player2WinPercent = result.player2WinPercent,
                    tiePercent = result.tiePercent,
                    isCalculating = false,
                    calculationDone = true,
                    errorMessage = null
                )

                Log.d(TAG, "Calculation finished p1=${result.player1WinPercent} p2=${result.player2WinPercent} tie=${result.tiePercent}")
            } catch (e: Exception) {
                Log.e(TAG, "Calculation error", e)
                _state.value = _state.value.copy(isCalculating = false, calculationDone = false, errorMessage = "Calculation error: ${e.message}")
            }
        }
    }

    private fun isCardUsed(card: Card): Boolean {
        val state = _state.value
        return card in state.player1Cards || card in state.player2Cards ||
               card in state.flopCards || card == state.turnCard || card == state.riverCard
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}
