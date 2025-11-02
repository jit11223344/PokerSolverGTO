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
import com.example.pokersolverGTO.engine.Action
import com.example.pokersolverGTO.engine.GTOStrategy
import com.example.pokersolverGTO.engine.Position
import com.example.pokersolverGTO.model.Card
import com.example.pokersolverGTO.model.Deck
import com.example.pokersolverGTO.ui.components.ActionButton
import com.example.pokersolverGTO.ui.components.HoleCards
import com.example.pokersolverGTO.ui.components.StatsCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PreflopState(
    val heroCards: List<Card> = emptyList(),
    val position: Position = Position.BTN,
    val facingRaise: Boolean = false,
    val result: PreflopResult? = null,
    val handsPlayed: Int = 0,
    val correctAnswers: Int = 0
)

data class PreflopResult(
    val isCorrect: Boolean,
    val explanation: String,
    val gtoRecommendation: String
)

class PreflopViewModel : ViewModel() {
    private val _state = MutableStateFlow(PreflopState())
    val state: StateFlow<PreflopState> = _state

    init { dealNewHand() }

    fun dealNewHand() {
        val deck = Deck().apply { shuffle() }
        val cards = deck.deal(2)
        val randomPosition = Position.values().random()
        _state.value = PreflopState(heroCards = cards, position = randomPosition, facingRaise = kotlin.random.Random.nextBoolean())
    }

    fun checkAction(action: Action) {
        viewModelScope.launch {
            val current = _state.value
            val (isCorrect, explanation) = GTOStrategy.isPreflopActionCorrect(current.heroCards, current.position, action, current.facingRaise)
            val gtoRec = GTOStrategy.getGTORecommendation(current.heroCards, current.position)
            _state.value = current.copy(
                result = PreflopResult(isCorrect, explanation, gtoRec),
                handsPlayed = current.handsPlayed + 1,
                correctAnswers = if (isCorrect) current.correctAnswers + 1 else current.correctAnswers
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreflopTrainerScreen(
    onBackPressed: () -> Unit,
    viewModel: PreflopViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Preflop Trainer") }, navigationIcon = { IconButton(onClick = onBackPressed) { Text("←", fontSize = 24.sp) } }) },
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

            Spacer(modifier = Modifier.height(16.dp))

            // Position indicator
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E))) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Position: ${state.position.name}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                    if (state.facingRaise) Text(text = "Facing a raise", fontSize = 16.sp, color = Color(0xFFE74C3C))
                }
            }

            // Hero cards
            HoleCards(cards = state.heroCards, modifier = Modifier.padding(vertical = 24.dp))

            // Action buttons
            if (state.result == null) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "What should you do?", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Medium)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionButton(text = "Fold", onClick = { viewModel.checkAction(Action.FOLD) }, color = Color(0xFFE74C3C), modifier = Modifier.weight(1f))
                        ActionButton(text = "Call", onClick = { viewModel.checkAction(Action.CALL) }, color = Color(0xFF3498DB), modifier = Modifier.weight(1f))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionButton(text = "Raise 2.5x", onClick = { viewModel.checkAction(Action.RAISE_2_5X) }, color = Color(0xFF27AE60), modifier = Modifier.weight(1f))
                        ActionButton(text = "Raise 3x", onClick = { viewModel.checkAction(Action.RAISE_3X) }, color = Color(0xFF27AE60), modifier = Modifier.weight(1f))
                    }

                    ActionButton(text = "All-In", onClick = { viewModel.checkAction(Action.ALL_IN) }, color = Color(0xFF9B59B6), modifier = Modifier.fillMaxWidth())
                }
            } else {
                // Show result
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (state.result!!.isCorrect) Color(0xFF27AE60) else Color(0xFFE74C3C))) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = if (state.result!!.isCorrect) "✓ Correct!" else "✗ Incorrect", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = state.result!!.explanation, fontSize = 16.sp, color = Color.White)
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.3f))
                        Text(text = state.result!!.gtoRecommendation, fontSize = 14.sp, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                    }
                }

                Button(onClick = { viewModel.dealNewHand() }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB))) {
                    Text("Next Hand", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
