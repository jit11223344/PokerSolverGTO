package com.example.pokersolverGTO.trainer.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokersolverGTO.R
import com.example.pokersolverGTO.model.Card
import com.example.pokersolverGTO.trainer.models.*
import com.example.pokersolverGTO.trainer.viewmodel.PokerTrainerViewModel
import com.example.pokersolverGTO.ui.components.PlayingCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokerTrainingTableScreen(
    onBackPressed: () -> Unit,
    mode: TrainingMode,
    viewModel: PokerTrainerViewModel = viewModel()
) {
    LaunchedEffect(mode) {
        viewModel.setTrainingMode(mode)
    }

    val scenario by viewModel.currentScenario.collectAsState()
    val recommendation by viewModel.lastRecommendation.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val stats by viewModel.playerStats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mode.name.replace('_', ' ').lowercase().capitalize()) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    // XP and Level display
                    PlayerStatsHeader(stats)
                }
            )
        },
        containerColor = Color(0xFF0D1B2A)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            scenario?.let { currentScenario ->
                PokerTableContent(
                    scenario = currentScenario,
                    recommendation = recommendation,
                    isProcessing = isProcessing,
                    onAction = { action, amount -> viewModel.submitAction(action, amount) },
                    onNextHand = { viewModel.nextHand() }
                )
            }
        }
    }
}

@Composable
private fun PlayerStatsHeader(stats: PlayerStats) {
    Row(
        modifier = Modifier.padding(end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Level badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(PlayerRanks.getRank(stats.currentLevel).color)
        ) {
            Text(
                text = "Lv ${stats.currentLevel}",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
        }

        // XP progress
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${stats.totalXP % stats.xpToNextLevel}/${stats.xpToNextLevel} XP",
                fontSize = 12.sp,
                color = Color(0xFFFFD700)
            )
            LinearProgressIndicator(
                progress = stats.levelProgress,
                modifier = Modifier
                    .width(80.dp)
                    .height(4.dp),
                color = Color(0xFFFFD700),
                trackColor = Color.Gray.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun PokerTableContent(
    scenario: HandScenario,
    recommendation: GTORecommendation?,
    isProcessing: Boolean,
    onAction: (PokerAction, Int) -> Unit,
    onNextHand: () -> Unit
) {
    var showCards by remember { mutableStateOf(false) }
    val cardsScale by animateFloatAsState(
        targetValue = if (showCards) 1f else 0.8f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )

    LaunchedEffect(scenario) {
        showCards = false
        kotlinx.coroutines.delay(200)
        showCards = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pot and position info
        ScenarioInfoBar(scenario)

        Spacer(modifier = Modifier.height(24.dp))

        // SVG Table background with cards
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            // Poker table SVG
            Image(
                painter = painterResource(id = R.drawable.poker_table),
                contentDescription = "Poker Table",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            // Cards overlay
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Community cards
                if (scenario.board.cards.isNotEmpty()) {
                    AnimatedVisibility(
                        visible = showCards,
                        enter = fadeIn() + scaleIn(initialScale = 0.8f),
                        exit = fadeOut() + scaleOut(targetScale = 0.8f)
                    ) {
                        CommunityCardsDisplay(scenario.board.cards)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Hero cards
                AnimatedVisibility(
                    visible = showCards,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut() + scaleOut(targetScale = 0.8f)
                ) {
                    HeroCardsDisplay(
                        card1 = scenario.heroHand.card1,
                        card2 = scenario.heroHand.card2,
                        scale = cardsScale
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recommendation panel or action buttons
        if (recommendation != null) {
            RecommendationPanel(
                recommendation = recommendation,
                onNextHand = onNextHand
            )
        } else {
            ActionButtonsPanel(
                scenario = scenario,
                isProcessing = isProcessing,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun ScenarioInfoBar(scenario: HandScenario) {
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
            // Position
            InfoChip(
                label = "Position",
                value = scenario.position.name,
                color = Color(0xFF3498DB)
            )

            // Pot
            InfoChip(
                label = "Pot",
                value = "$${scenario.potSize}",
                color = Color(0xFFFFD700)
            )

            // Stack
            InfoChip(
                label = "Stack",
                value = "$${scenario.stackSize}",
                color = Color(0xFF27AE60)
            )

            // Facing bet
            if (scenario.facingBet > 0) {
                InfoChip(
                    label = "Bet",
                    value = "$${scenario.facingBet}",
                    color = Color(0xFFE74C3C)
                )
            }
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun CommunityCardsDisplay(cards: List<Card>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        cards.forEach { card ->
            PlayingCard(card = card)
        }
    }
}

@Composable
private fun HeroCardsDisplay(card1: Card, card2: Card, scale: Float) {
    Card(
        modifier = Modifier.scale(scale),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlayingCard(card = card1, modifier = Modifier.size(width = 64.dp, height = 88.dp))
            PlayingCard(card = card2, modifier = Modifier.size(width = 64.dp, height = 88.dp))
        }
    }
}

@Composable
private fun ActionButtonsPanel(
    scenario: HandScenario,
    isProcessing: Boolean,
    onAction: (PokerAction, Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (scenario.facingBet > 0) {
            // Facing a bet - show Fold/Call/Raise
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "Fold",
                    color = Color(0xFFE74C3C),
                    enabled = !isProcessing,
                    modifier = Modifier.weight(1f),
                    onClick = { onAction(PokerAction.FOLD, 0) }
                )
                ActionButton(
                    text = "Call $${scenario.facingBet}",
                    color = Color(0xFF3498DB),
                    enabled = !isProcessing,
                    modifier = Modifier.weight(1f),
                    onClick = { onAction(PokerAction.CALL, scenario.facingBet) }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "Raise 2.5x",
                    color = Color(0xFF27AE60),
                    enabled = !isProcessing,
                    modifier = Modifier.weight(1f),
                    onClick = { onAction(PokerAction.RAISE, (scenario.facingBet * 2.5f).toInt()) }
                )
                ActionButton(
                    text = "Raise 3x",
                    color = Color(0xFF27AE60),
                    enabled = !isProcessing,
                    modifier = Modifier.weight(1f),
                    onClick = { onAction(PokerAction.RAISE, scenario.facingBet * 3) }
                )
            }
        } else {
            // No bet - show Check/Bet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "Check",
                    color = Color(0xFF95A5A6),
                    enabled = !isProcessing,
                    modifier = Modifier.weight(1f),
                    onClick = { onAction(PokerAction.CHECK, 0) }
                )
                ActionButton(
                    text = "Bet 50%",
                    color = Color(0xFF27AE60),
                    enabled = !isProcessing,
                    modifier = Modifier.weight(1f),
                    onClick = { onAction(PokerAction.RAISE, scenario.potSize / 2) }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "Bet 75%",
                    color = Color(0xFF27AE60),
                    enabled = !isProcessing,
                    modifier = Modifier.weight(1f),
                    onClick = { onAction(PokerAction.RAISE, (scenario.potSize * 0.75f).toInt()) }
                )
                ActionButton(
                    text = "Bet Pot",
                    color = Color(0xFF27AE60),
                    enabled = !isProcessing,
                    modifier = Modifier.weight(1f),
                    onClick = { onAction(PokerAction.RAISE, scenario.potSize) }
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    color: Color,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = color.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RecommendationPanel(
    recommendation: GTORecommendation,
    onNextHand: () -> Unit
) {
    val isCorrect = recommendation.evDifference <= 0.01f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) Color(0xFF27AE60) else Color(0xFFE74C3C)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Result header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isCorrect) "✓ Correct!" else "✗ Suboptimal",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = if (isCorrect) "+${(recommendation.evDifference * -10).toInt()} XP"
                           else "EV: ${String.format("%.2f", recommendation.evDifference)}",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(color = Color.White.copy(alpha = 0.3f))

            // Explanation
            Text(
                text = recommendation.explanation,
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 20.sp
            )

            // GTO frequencies
            Text(
                text = "GTO Action Mix:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            recommendation.actionFrequencies.entries.sortedByDescending { it.value }.forEach { (action, freq) ->
                FrequencyBar(action.name, freq)
            }

            // Range advice
            recommendation.rangeAdvice?.let { advice ->
                Divider(color = Color.White.copy(alpha = 0.3f))
                Text(
                    text = advice,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 16.sp
                )
            }

            // Next hand button
            Button(
                onClick = onNextHand,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Next Hand →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrect) Color(0xFF27AE60) else Color(0xFFE74C3C)
                )
            }
        }
    }
}

@Composable
private fun FrequencyBar(actionName: String, frequency: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = actionName,
            fontSize = 12.sp,
            color = Color.White,
            modifier = Modifier.width(60.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(20.dp)
                .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(frequency)
                    .background(Color.White, RoundedCornerShape(10.dp))
            )
        }

        Text(
            text = "${(frequency * 100).toInt()}%",
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(40.dp)
        )
    }
}
