package com.example.pokersolverGTO.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokersolverGTO.navigation.Screen

data class Exercise(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerScreen(onNavigateToExercise: (String) -> Unit) {
    val exercises = listOf(
        Exercise("preflop", "Preflop GTO Trainer", "Master opening ranges, 3-bets, and preflop decisions by position", "🎯", Color(0xFF3498DB)),
        Exercise("postflop", "Postflop Strategy", "Practice continuation bets, turn/river play with equity calculations", "🎲", Color(0xFF27AE60)),
        Exercise("hand_ranking", "Hand Ranking Quiz", "Test your ability to compare poker hands instantly", "🃏", Color(0xFF9B59B6)),
        Exercise("best_hand", "Best 5-Card Hand", "Identify the strongest 5-card combination from 7 cards", "🔍", Color(0xFFE74C3C)),
        Exercise("pot_odds", "Pot Odds & EV Calculator", "Master pot odds, implied odds, and expected value", "🧮", Color(0xFFF39C12)),
        Exercise("gto_scenario", "GTO Database Solver", "Query pre-solved GTO scenarios from the solution database", "💎", Color(0xFF8E44AD))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GTO Poker Training Hub") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2C3E50))
            )
        },
        containerColor = Color(0xFF1A1A2E)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🏆 6 Core Training Exercises",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Master GTO poker strategy with interactive drills, real-time feedback, and progress tracking",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                }
            }

            // Exercise cards
            exercises.forEach { exercise ->
                ExerciseCard(exercise = exercise) { onNavigateToExercise(exercise.id) }
            }

            // Poker Table Demo Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFF2ECC71).copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✨", fontSize = 32.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Poker Table Visualizer",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "See the polished SVG poker table with animated dealing",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            lineHeight = 18.sp
                        )
                    }

                    Button(
                        onClick = { onNavigateToExercise("poker_table") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71))
                    ) {
                        Text("View →", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseCard(exercise: Exercise, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(64.dp).background(exercise.color.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Text(text = exercise.icon, fontSize = 32.sp) }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = exercise.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = exercise.description, fontSize = 14.sp, color = Color.Gray, lineHeight = 18.sp)
            }

            Text(text = "→", fontSize = 24.sp, color = exercise.color)
        }
    }
}
