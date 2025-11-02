package com.example.pokersolverGTO.ui.screens.exercises

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.pokersolverGTO.model.Deck
import com.example.pokersolverGTO.model.HandRank
import com.example.pokersolverGTO.ui.components.CommunityCards
import com.example.pokersolverGTO.ui.components.HoleCards
import com.example.pokersolverGTO.ui.components.PlayingCard
import com.example.pokersolverGTO.ui.components.StatsCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// State + Result

data class BestHandState(
    val heroCards: List<Card> = emptyList(),
    val board: List<Card> = emptyList(),
    val result: BestHandResult? = null,
    val handsPlayed: Int = 0,
    val correctAnswers: Int = 0
)

data class BestHandResult(
    val isCorrect: Boolean,
    val correctRank: HandRank,
    val description: String,
    val bestCards: List<Card>
)

// ViewModel

class BestHandViewModel : ViewModel() {
    private val _state = MutableStateFlow(BestHandState())
    val state: StateFlow<BestHandState> = _state

    init { generateNewScenario() }

    fun generateNewScenario() {
        val deck = Deck().apply { shuffle() }
        val heroCards = deck.deal(2)
        val board = deck.deal(5)
        _state.value = BestHandState(heroCards = heroCards, board = board)
    }

    fun checkAnswer(selectedRank: HandRank) {
        val current = _state.value
        val evaluation = HandEvaluator.evaluateHand(current.heroCards + current.board)
        val isCorrect = selectedRank == evaluation.handRank
        _state.value = current.copy(
            result = BestHandResult(
                isCorrect = isCorrect,
                correctRank = evaluation.handRank,
                description = evaluation.description,
                bestCards = evaluation.cards
            ),
            handsPlayed = current.handsPlayed + 1,
            correctAnswers = if (isCorrect) current.correctAnswers + 1 else current.correctAnswers
        )
    }
}

// UI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BestHandIdentifierScreen(
    onBackPressed: () -> Unit,
    viewModel: BestHandViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Best Hand Identifier") }, navigationIcon = { IconButton(onClick = onBackPressed) { Text("←", fontSize = 24.sp) } }) },
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatsCard(title = "Hands", value = state.handsPlayed.toString())
                StatsCard(
                    title = "Accuracy",
                    value = if (state.handsPlayed > 0) "${(state.correctAnswers * 100 / state.handsPlayed)}%" else "0%"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Identify your best 5-card hand",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Your cards
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E))) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Your Cards", fontSize = 16.sp, color = Color.Gray)
                    HoleCards(cards = state.heroCards)
                }
            }

            // Board
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E))) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Board", fontSize = 16.sp, color = Color.Gray)
                    CommunityCards(cards = state.board)
                }
            }

            // Hand rank options or result
            if (state.result == null) {
                Text(text = "Select your hand rank:", fontSize = 16.sp, color = Color.White)
                LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(HandRank.values().reversed()) { rank ->
                        Button(
                            onClick = { viewModel.checkAnswer(rank) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C3E50))
                        ) { Text(text = rank.displayName, fontSize = 16.sp) }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (state.result!!.isCorrect) Color(0xFF27AE60) else Color(0xFFE74C3C))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (state.result!!.isCorrect) "✓ Correct!" else "✗ Incorrect",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Best Hand: ${state.result!!.correctRank.displayName}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Text(text = state.result!!.description, fontSize = 16.sp, color = Color.White)
                        Divider(color = Color.White.copy(alpha = 0.3f))
                        Text(text = "Best 5 cards:", fontSize = 14.sp, color = Color.Gray)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            state.result!!.bestCards.take(5).forEach { card ->
                                PlayingCard(card = card, modifier = Modifier.size(width = 40.dp, height = 56.dp))
                            }
                        }
                    }
                }
                Button(
                    onClick = { viewModel.generateNewScenario() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB))
                ) { Text("Next Hand", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}
