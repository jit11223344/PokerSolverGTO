package com.example.pokersolverGTO.trainer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokersolverGTO.trainer.models.*
import com.example.pokersolverGTO.trainer.viewmodel.PokerTrainerViewModel
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerHomeScreen(
    onModeSelected: (TrainingMode) -> Unit,
    onViewStats: () -> Unit,
    viewModel: PokerTrainerViewModel = viewModel()
) {
    val stats by viewModel.playerStats.collectAsState()
    val recentHands by viewModel.recentHands.collectAsState()

    // Removed Scaffold and TopAppBar - MainActivity provides the app bar and drawer
    // Content now uses full available space without duplicate header
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Player card
        item {
            PlayerStatsCard(stats, onViewStats)
        }

        // Daily streak
        item {
            DailyStreakCard(stats)
        }

        // Training modes
        item {
            Text(
                text = "Training Modes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            TrainingModeCard(
                title = "Preflop Trainer",
                description = "Master opening ranges and 3-bet strategy",
                icon = Icons.Default.Star,
                color = Color(0xFF3498DB),
                stats = "Accuracy: ${(stats.preflopAccuracy * 100).toInt()}%",
                onClick = { onModeSelected(TrainingMode.PREFLOP) }
            )
        }

        item {
            TrainingModeCard(
                title = "Postflop Trainer",
                description = "Practice flop/turn/river decisions with GTO",
                icon = Icons.Default.PlayArrow,
                color = Color(0xFF27AE60),
                stats = "Accuracy: ${(stats.postflopAccuracy * 100).toInt()}%",
                onClick = { onModeSelected(TrainingMode.POSTFLOP) }
            )
        }

        item {
            TrainingModeCard(
                title = "Quiz Mode",
                description = "Timed challenges to test your skills",
                icon = Icons.Default.DateRange,
                color = Color(0xFFFF9800),
                stats = "Best Streak: ${stats.bestStreak}",
                onClick = { onModeSelected(TrainingMode.QUIZ) }
            )
        }

        item {
            TrainingModeCard(
                title = "Sandbox Mode",
                description = "Free play with instant feedback",
                icon = Icons.Default.Build,
                color = Color(0xFF9C27B0),
                stats = "${stats.totalHands} hands played",
                onClick = { onModeSelected(TrainingMode.SANDBOX) }
            )
        }

        // Recent hands
        if (recentHands.isNotEmpty()) {
            item {
                Text(
                    text = "Recent Hands",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(recentHands.take(5)) { hand ->
                RecentHandItem(hand)
            }
        }
    }
}

@Composable
private fun PlayerStatsCard(stats: PlayerStats, onViewStats: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewStats),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B263B)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1B263B),
                            Color(PlayerRanks.getRank(stats.currentLevel).color).copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar and rank
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PokerChipAvatar(stats)

                    Column {
                        Text(
                            text = PlayerRanks.getRank(stats.currentLevel).title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Level ${stats.currentLevel}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Stats summary
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${stats.totalXP} XP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${stats.totalHands} hands",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun PokerChipAvatar(stats: PlayerStats) {
    val rank = PlayerRanks.getRank(stats.currentLevel)

    // Classic casino colors based on rank
    val chipColors = when (rank.title) {
        "Fish" -> listOf(Color(0xFF8B0000), Color(0xFFFFD700), Color(0xFF8B0000)) // Burgundy/Gold
        "Calling Station" -> listOf(Color(0xFF006400), Color(0xFFFFD700), Color(0xFF006400)) // Dark Green/Gold
        "Rock" -> listOf(Color(0xFF000080), Color(0xFFFFD700), Color(0xFF000080)) // Navy/Gold
        "Shark" -> listOf(Color(0xFF8B008B), Color(0xFFFFD700), Color(0xFF8B008B)) // Dark Magenta/Gold
        "Pro" -> listOf(Color(0xFFFF8C00), Color(0xFFFFD700), Color(0xFFFF8C00)) // Dark Orange/Gold
        else -> listOf(Color(0xFFFFD700), Color(0xFFFFFF), Color(0xFFFFD700)) // Gold/White for Master
    }

    Box(
        modifier = Modifier
            .size(80.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                spotColor = Color.Black.copy(alpha = 0.5f)
            )
            .graphicsLayer {
                // Subtle 3D effect
                shadowElevation = 4f
                shape = CircleShape
                clip = true
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer glow effect
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            chipColors[1].copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Main chip body with gradient
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    Brush.verticalGradient(
                        colors = chipColors
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.8f),
                            Color.Black.copy(alpha = 0.2f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Inner ring for depth
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = chipColors.reversed()
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.5f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Center circle with shine effect
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.9f),
                                    chipColors[1].copy(alpha = 0.7f),
                                    chipColors[0]
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Text with shadow
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = rank.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.White.copy(alpha = 0.8f),
                                    offset = Offset(1f, 1f),
                                    blurRadius = 2f
                                )
                            )
                        )
                        Text(
                            text = "Lv.${stats.currentLevel}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.White.copy(alpha = 0.8f),
                                    offset = Offset(0.5f, 0.5f),
                                    blurRadius = 1f
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyStreakCard(stats: PlayerStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatBox("Today", "${stats.handsPlayedToday}", Color(0xFF3498DB))
            StatBox("Streak", "${stats.currentStreak}", Color(0xFFFF9800))
            StatBox("Best", "${stats.bestStreak}", Color(0xFFE74C3C))
            StatBox("Accuracy", "${(stats.accuracy * 100).toInt()}%", Color(0xFF27AE60))
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun TrainingModeCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    stats: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stats,
                    fontSize = 12.sp,
                    color = color,
                    fontWeight = FontWeight.Medium
                )
            }

            // Arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Go",
                tint = Color.Gray
            )
        }
    }
}

@Composable
private fun RecentHandItem(hand: TrainingHandResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (hand.isCorrect) Color(0xFF27AE60).copy(alpha = 0.1f)
            else Color(0xFFE74C3C).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${hand.heroHandNotation} - ${hand.position.name}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${hand.mode.name} • ${hand.street.name}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (hand.isCorrect) "✓" else "✗",
                    fontSize = 20.sp,
                    color = if (hand.isCorrect) Color(0xFF27AE60) else Color(0xFFE74C3C)
                )
                Text(
                    text = "+${hand.xpEarned} XP",
                    fontSize = 12.sp,
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
