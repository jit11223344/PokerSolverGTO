package com.example.pokersolverGTO.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokersolverGTO.training.TrainingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GTOScenarioScreen(
    onBackPressed: () -> Unit,
    viewModel: TrainingViewModel = viewModel()
) {
    val scenario by viewModel.currentScenario.collectAsState()
    val stats by viewModel.stats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GTO Scenario Viewer") },
                navigationIcon = { IconButton(onClick = onBackPressed) { Text("←", fontSize = 24.sp) } }
            )
        },
        containerColor = Color(0xFF1A1A2E)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem("Total", stats.totalQuestions.toString())
                        StatItem("Correct", stats.correctAnswers.toString())
                        StatItem("Accuracy", "${(stats.accuracy * 100).toInt()}%")
                    }
                }
            }

            // Current scenario
            item {
                scenario?.let { s ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Scenario: ${s.scenario_id}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                InfoChip("Position", s.position)
                                InfoChip("Stack", "${s.stack_depth}bb")
                            }

                            Text(
                                text = "Board: ${s.board.joinToString(" ")}",
                                fontSize = 16.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "Action History: ${s.action_history.joinToString(" → ")}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            Divider(color = Color.Gray.copy(alpha = 0.3f))

                            Text(
                                text = "GTO Strategy:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            s.gto_strategy.entries.sortedByDescending { it.value }.forEach { (action, freq) ->
                                StrategyRow(action, freq)
                            }
                        }
                    }
                } ?: run {
                    Text(
                        text = "Loading scenario...",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Action buttons
            item {
                scenario?.let {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        it.gto_strategy.keys.forEach { action ->
                            Button(
                                onClick = { viewModel.submitAnswer(action) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB))
                            ) {
                                Text("Choose: $action", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { viewModel.loadRandomScenario() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                        ) {
                            Text("Load Random Scenario", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
private fun InfoChip(label: String, value: String) {
    Row(
        modifier = Modifier
            .background(Color(0xFF2C3E50), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = "$label:", fontSize = 14.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun StrategyRow(action: String, frequency: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2C3E50), RoundedCornerShape(6.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = action, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Frequency bar
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(20.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(frequency)
                        .background(Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                )
            }

            Text(
                text = "${(frequency * 100).toInt()}%",
                fontSize = 14.sp,
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
