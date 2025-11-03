package com.example.pokersolverGTO.trainer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokersolverGTO.trainer.models.*
import com.example.pokersolverGTO.trainer.viewmodel.PokerTrainerViewModel
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsDetailScreen(
    onBackPressed: () -> Unit,
    viewModel: PokerTrainerViewModel = viewModel()
) {
    val stats by viewModel.playerStats.collectAsState()
    val recentHands by viewModel.recentHands.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Stats") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B263B)
                )
            )
        },
        containerColor = Color(0xFF0D1B2A)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overall stats
            item {
                OverallStatsCard(stats)
            }

            // Performance by mode
            item {
                Text(
                    text = "Performance by Mode",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            item {
                ModePerformanceCard(
                    mode = "Preflop",
                    accuracy = stats.preflopAccuracy,
                    color = Color(0xFF3498DB)
                )
            }

            item {
                ModePerformanceCard(
                    mode = "Postflop",
                    accuracy = stats.postflopAccuracy,
                    color = Color(0xFF27AE60)
                )
            }

            // Accuracy trend chart
            item {
                Text(
                    text = "Accuracy Trend",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            item {
                AccuracyTrendChart(recentHands.take(20).reversed())
            }

            // Achievements
            if (stats.achievements.isNotEmpty()) {
                item {
                    Text(
                        text = "Achievements",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                item {
                    AchievementsGrid(stats.achievements)
                }
            }

            // Milestones
            item {
                Text(
                    text = "Next Milestones",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            item {
                MilestonesCard(stats)
            }
        }
    }
}

@Composable
private fun OverallStatsCard(stats: PlayerStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Overall Performance",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Key metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricColumn(
                    label = "Hands",
                    value = "${stats.totalHands}",
                    color = Color(0xFF3498DB)
                )
                MetricColumn(
                    label = "Accuracy",
                    value = "${(stats.accuracy * 100).toInt()}%",
                    color = Color(0xFF27AE60)
                )
                MetricColumn(
                    label = "Avg EV Loss",
                    value = String.format(Locale.US, "%.2f", abs(stats.averageEVLoss)),
                    color = Color(0xFFE74C3C)
                )
            }

            HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))

            // Secondary metrics
            StatRow("Average Decision Time", "${stats.averageDecisionTime / 1000}s")
            StatRow("Current Streak", "${stats.currentStreak} hands")
            StatRow("Best Streak", "${stats.bestStreak} hands")
            StatRow("Total XP Earned", "${stats.totalXP}")
        }
    }
}

@Composable
private fun MetricColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ModePerformanceCard(mode: String, accuracy: Float, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = mode,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${(accuracy * 100).toInt()}% Accuracy",
                    fontSize = 14.sp,
                    color = color
                )
            }

            // Circular progress
            CircularProgressIndicator(
                progress = { accuracy },
                modifier = Modifier.size(60.dp),
                color = color,
                strokeWidth = 6.dp,
                trackColor = Color.Gray.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun AccuracyTrendChart(hands: List<TrainingHandResult>) {
    if (hands.isEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Play more hands to see trends",
                    color = Color.Gray
                )
            }
        }
        return
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val points = hands.size

                if (points < 2) return@Canvas

                val xStep = width / (points - 1)

                // Calculate moving accuracy (last N hands)
                val accuracies = mutableListOf<Float>()
                var correct = 0
                hands.forEachIndexed { index, hand ->
                    if (hand.isCorrect) correct++
                    val accuracy = correct.toFloat() / (index + 1)
                    accuracies.add(accuracy)
                }

                // Draw grid lines
                for (i in 0..4) {
                    val y = height * (i / 4f)
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.2f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                // Draw accuracy line
                val path = Path().apply {
                    moveTo(0f, height * (1f - accuracies[0]))
                    accuracies.forEachIndexed { index, accuracy ->
                        val x = index * xStep
                        val y = height * (1f - accuracy)
                        lineTo(x, y)
                    }
                }

                drawPath(
                    path = path,
                    color = Color(0xFF27AE60),
                    style = Stroke(width = 3f)
                )

                // Draw points
                accuracies.forEachIndexed { index, accuracy ->
                    val x = index * xStep
                    val y = height * (1f - accuracy)
                    drawCircle(
                        color = Color(0xFF27AE60),
                        radius = 4f,
                        center = Offset(x, y)
                    )
                }
            }

            // Labels
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("100%", fontSize = 10.sp, color = Color.Gray)
                Text("75%", fontSize = 10.sp, color = Color.Gray)
                Text("50%", fontSize = 10.sp, color = Color.Gray)
                Text("25%", fontSize = 10.sp, color = Color.Gray)
                Text("0%", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun AchievementsGrid(achievements: List<String>) {
    val achievementInfo = mapOf(
        "first_hand" to ("First Hand" to "🎉"),
        "century" to ("Century" to "💯"),
        "thousand" to ("Millennium" to "🏆"),
        "streak_10" to ("10 Streak" to "🔥"),
        "streak_50" to ("50 Streak" to "⚡"),
        "level_10" to ("Level 10" to "⭐"),
        "level_50" to ("Level 50" to "👑"),
        "high_accuracy" to ("Sharpshooter" to "🎯")
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            achievements.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { achievement ->
                        val (title, emoji) = achievementInfo[achievement] ?: ("Achievement" to "🏅")
                        AchievementBadge(title, emoji)
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.AchievementBadge(title: String, emoji: String) {
    Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700).copy(alpha = 0.2f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MilestonesCard(stats: PlayerStats) {
    val milestones = listOf(
        Milestone("Next Level", stats.xpToNextLevel - (stats.totalXP % stats.xpToNextLevel), "XP needed"),
        Milestone("1000 Hands", maxOf(0, 1000 - stats.totalHands), "hands to go"),
        Milestone("90% Accuracy", if (stats.accuracy < 0.9f) ((0.9f - stats.accuracy) * 100).toInt() else 0, "% improvement needed")
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            milestones.forEach { milestone ->
                MilestoneRow(milestone)
            }
        }
    }
}

data class Milestone(val title: String, val remaining: Int, val unit: String)

@Composable
private fun MilestoneRow(milestone: Milestone) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = milestone.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = "${milestone.remaining} ${milestone.unit}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFFFD700)
        )
    }
}
