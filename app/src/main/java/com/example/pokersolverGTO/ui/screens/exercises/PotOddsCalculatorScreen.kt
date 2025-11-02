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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokersolverGTO.ui.components.ActionButton
import com.example.pokersolverGTO.ui.components.StatsCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

// State + Result models

data class PotOddsState(
    val pot: Int = 0,
    val betToCall: Int = 0,
    val equity: Int = 0,
    val result: PotOddsResult? = null,
    val handsPlayed: Int = 0,
    val correctAnswers: Int = 0
)

data class PotOddsResult(
    val isCorrect: Boolean,
    val explanation: String,
    val potOdds: Float,
    val correctAction: String
)

// ViewModel

class PotOddsViewModel : ViewModel() {
    private val _state = MutableStateFlow(PotOddsState())
    val state: StateFlow<PotOddsState> = _state

    init { generateNewScenario() }

    fun generateNewScenario() {
        val pot = listOf(50, 75, 100, 150, 200, 250, 300).random()
        val betToCall = listOf(pot / 4, pot / 3, pot / 2, pot * 2 / 3, pot).random()
        val equity = Random.nextInt(15, 85)
        _state.value = PotOddsState(pot = pot, betToCall = betToCall, equity = equity)
    }

    fun checkAnswer(action: String) {
        val current = _state.value
        val totalPot = current.pot + current.betToCall
        val potOdds = (current.betToCall.toFloat() / totalPot) * 100
        val correctAction = if (current.equity > potOdds) "call" else "fold"
        val isCorrect = action == correctAction

        val explanation = buildString {
            append("Pot odds: ${current.betToCall} / ${totalPot} = ${String.format("%.1f", potOdds)}%\n")
            append("Your equity: ${current.equity}%\n\n")
            if (current.equity > potOdds) {
                val profit = current.equity - potOdds
                append("Since ${current.equity}% > ${String.format("%.1f", potOdds)}%, ")
                append("calling is +EV by ${String.format("%.1f", profit)}%.\n")
                append("Correct play: CALL")
            } else {
                val loss = potOdds - current.equity
                append("Since ${current.equity}% < ${String.format("%.1f", potOdds)}%, ")
                append("calling is -EV by ${String.format("%.1f", loss)}%.\n")
                append("Correct play: FOLD")
            }
        }

        _state.value = current.copy(
            result = PotOddsResult(isCorrect, explanation, potOdds, correctAction),
            handsPlayed = current.handsPlayed + 1,
            correctAnswers = if (isCorrect) current.correctAnswers + 1 else current.correctAnswers
        )
    }
}

// UI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotOddsCalculatorScreen(
    onBackPressed: () -> Unit,
    viewModel: PotOddsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Pot Odds Calculator") }, navigationIcon = { IconButton(onClick = onBackPressed) { Text("←", fontSize = 24.sp) } }) },
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
                StatsCard(title = "Scenarios", value = state.handsPlayed.toString())
                StatsCard(
                    title = "Accuracy",
                    value = if (state.handsPlayed > 0) "${(state.correctAnswers * 100 / state.handsPlayed)}%" else "0%"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Calculate if you should call or fold",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Scenario info
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E))) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Current Pot:", fontSize = 18.sp, color = Color.White)
                        Text(text = "$${state.pot}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                    }
                    Divider(color = Color.White.copy(alpha = 0.2f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Bet to Call:", fontSize = 18.sp, color = Color.White)
                        Text(text = "$${state.betToCall}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE74C3C))
                    }
                    Divider(color = Color.White.copy(alpha = 0.2f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Your Equity:", fontSize = 18.sp, color = Color.White)
                        Text(text = "${state.equity}%", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF27AE60))
                    }
                }
            }

            // Hint
            if (state.result == null) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF2C3E50))) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "💡 Hint:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                        Text(text = "Pot odds = Bet to call / (Pot + Bet to call)", fontSize = 14.sp, color = Color.White)
                        Text(text = "Call if: Your equity > Pot odds", fontSize = 14.sp, color = Color.White)
                        Text(text = "Fold if: Your equity < Pot odds", fontSize = 14.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Answer buttons or result
            if (state.result == null) {
                Text(text = "Should you call or fold?", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.White)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ActionButton(text = "FOLD", onClick = { viewModel.checkAnswer("fold") }, color = Color(0xFFE74C3C), modifier = Modifier.weight(1f))
                    ActionButton(text = "CALL", onClick = { viewModel.checkAnswer("call") }, color = Color(0xFF27AE60), modifier = Modifier.weight(1f))
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (state.result!!.isCorrect) Color(0xFF27AE60) else Color(0xFFE74C3C))) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = if (state.result!!.isCorrect) "✓ Correct!" else "✗ Incorrect", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Divider(color = Color.White.copy(alpha = 0.3f))
                        Text(text = state.result!!.explanation, fontSize = 15.sp, color = Color.White, lineHeight = 22.sp)
                    }
                }
                Button(onClick = { viewModel.generateNewScenario() }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB))) {
                    Text("Next Scenario", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
