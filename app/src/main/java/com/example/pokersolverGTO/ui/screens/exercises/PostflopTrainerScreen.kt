package com.example.pokersolverGTO.ui.screens.exercises

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
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokersolverGTO.engine.MonteCarloSimulator
import com.example.pokersolverGTO.model.Card
import com.example.pokersolverGTO.model.Deck
import com.example.pokersolverGTO.ui.components.ActionButton
import com.example.pokersolverGTO.ui.components.CommunityCards
import com.example.pokersolverGTO.ui.components.HoleCards
import com.example.pokersolverGTO.ui.components.StatsCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PostflopState(
    val heroCards: List<Card> = emptyList(),
    val communityCards: List<Card> = emptyList(),
    val pot: Int = 100,
    val stack: Int = 1000,
    val equity: Float? = null,
    val result: PostflopResult? = null,
    val isCalculating: Boolean = false,
    val handsPlayed: Int = 0,
    val correctAnswers: Int = 0
)

data class PostflopResult(
    val isCorrect: Boolean,
    val explanation: String,
    val equity: Float
)

class PostflopViewModel : ViewModel() {
    private val _state = MutableStateFlow(PostflopState())
    val state: StateFlow<PostflopState> = _state

    init { dealNewHand() }

    fun dealNewHand() {
        viewModelScope.launch {
            val deck = Deck().apply { shuffle() }
            val heroCards = deck.deal(2)
            val flop = deck.deal(3)
            val pot = (50..200).random()
            val stack = (500..2000).random()
            _state.value = PostflopState(heroCards = heroCards, communityCards = flop, pot = pot, stack = stack)
            calculateEquity(heroCards, flop)
        }
    }

    private suspend fun calculateEquity(heroCards: List<Card>, board: List<Card>) {
        _state.value = _state.value.copy(isCalculating = true)
        val equity = MonteCarloSimulator.calculateEquity(heroCards, board, numOpponents = 1, simulations = 5000)
        _state.value = _state.value.copy(equity = equity, isCalculating = false)
    }

    fun checkAction(action: String, betSize: Float) {
        viewModelScope.launch {
            val current = _state.value
            val equity = current.equity ?: 0f
            val pot = current.pot.toFloat()
            val (isCorrect, explanation) = when (action) {
                "check" -> if (equity > 40) false to "With ${equity.toInt()}% equity, checking is passive. Consider betting for value." else true to "Good check with ${equity.toInt()}% equity."
                "bet" -> {
                    val potOddsNeeded = betSize / (pot + betSize) * 100
                    when {
                        equity > potOddsNeeded + 10 -> true to "Good value bet! Your ${equity.toInt()}% equity justifies this bet size."
                        equity < 30 -> true to "Acceptable bluff with ${equity.toInt()}% equity."
                        else -> false to "Marginal situation. Your ${equity.toInt()}% equity is borderline for this bet size."
                    }
                }
                "fold" -> if (equity > 50) false to "Too strong to fold! You have ${equity.toInt()}% equity." else true to "Reasonable fold with ${equity.toInt()}% equity."
                else -> false to "Unknown action"
            }
            _state.value = current.copy(
                result = PostflopResult(isCorrect, explanation, equity),
                handsPlayed = current.handsPlayed + 1,
                correctAnswers = if (isCorrect) current.correctAnswers + 1 else current.correctAnswers
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostflopTrainerScreen(
    onBackPressed: () -> Unit,
    viewModel: PostflopViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Postflop Trainer") }, navigationIcon = { IconButton(onClick = onBackPressed) { Text("←", fontSize = 24.sp) } }) },
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

            // Game state
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E))) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("Pot", fontSize = 14.sp, color = Color.Gray); Text("$${state.pot}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700)) }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("Stack", fontSize = 14.sp, color = Color.Gray); Text("$${state.stack}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Equity", fontSize = 14.sp, color = Color.Gray)
                        if (state.isCalculating) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("${state.equity?.toInt() ?: 0}%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF27AE60))
                    }
                }
            }

            // Community cards
            CommunityCards(cards = state.communityCards, modifier = Modifier.padding(vertical = 16.dp))

            // Hero cards
            HoleCards(cards = state.heroCards)

            Spacer(modifier = Modifier.height(8.dp))

            // Actions
            if (state.result == null && !state.isCalculating) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Choose your action:", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    ActionButton(text = "Check", onClick = { viewModel.checkAction("check", 0f) }, color = Color(0xFF95A5A6), modifier = Modifier.fillMaxWidth())
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionButton(text = "Bet 33%", onClick = { viewModel.checkAction("bet", state.pot * 0.33f) }, color = Color(0xFF27AE60), modifier = Modifier.weight(1f))
                        ActionButton(text = "Bet 50%", onClick = { viewModel.checkAction("bet", state.pot * 0.5f) }, color = Color(0xFF27AE60), modifier = Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionButton(text = "Bet 75%", onClick = { viewModel.checkAction("bet", state.pot * 0.75f) }, color = Color(0xFF27AE60), modifier = Modifier.weight(1f))
                        ActionButton(text = "Bet Pot", onClick = { viewModel.checkAction("bet", state.pot.toFloat()) }, color = Color(0xFF27AE60), modifier = Modifier.weight(1f))
                    }
                }
            } else if (state.result != null) {
                // Show result
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (state.result!!.isCorrect) Color(0xFF27AE60) else Color(0xFFE74C3C))) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = if (state.result!!.isCorrect) "✓ Good play!" else "✗ Suboptimal", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = state.result!!.explanation, fontSize = 16.sp, color = Color.White)
                    }
                }

                Button(onClick = { viewModel.dealNewHand() }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB))) {
                    Text("Next Hand", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
