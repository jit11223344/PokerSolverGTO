package com.example.pokersolverGTO.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokersolverGTO.R
import com.example.pokersolverGTO.model.Card
import com.example.pokersolverGTO.model.Deck
import com.example.pokersolverGTO.ui.components.ActionButton
import com.example.pokersolverGTO.ui.components.CommunityCards
import com.example.pokersolverGTO.ui.components.HoleCards
import com.example.pokersolverGTO.ui.components.PlayingCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokerTableScreen(onBackPressed: () -> Unit) {
    var deck by remember { mutableStateOf(Deck().apply { shuffle() }) }
    var board by remember { mutableStateOf<List<Card>>(emptyList()) }
    var hero by remember { mutableStateOf<List<Card>>(emptyList()) }
    val scope = rememberCoroutineScope()

    // simple deal animation: animate alpha from 0 -> 1 sequentially
    val cardAlphaAnims = remember { List(7) { Animatable(0f) } }

    fun dealNewHand() {
        deck = Deck().apply { shuffle() }
        hero = deck.deal(2)
        board = emptyList()
        scope.launch { cardAlphaAnims.forEach { it.snapTo(0f) } }
    }

    LaunchedEffect(Unit) { dealNewHand() }

    LaunchedEffect(board, hero) {
        // animate hero cards then flop/turn/river
        val order = listOf(0, 1, 2, 3, 4, 5, 6)
        order.forEachIndexed { i, idx ->
            delay(if (i == 0) 150 else 120)
            cardAlphaAnims[idx].animateTo(1f, tween(350))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Poker Table") },
                navigationIcon = { IconButton(onClick = onBackPressed) { Text("←", fontSize = 24.sp) } }
            )
        },
        containerColor = Color(0xFFEDEDED)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            TableWithSeats(
                hero = hero,
                board = board,
                cardAlpha = cardAlphaAnims.map { it.value },
                onDealFlop = { if (board.isEmpty()) board = deck.deal(3) },
                onDealTurn = { if (board.size == 3) board = board + deck.deal(1) },
                onDealRiver = { if (board.size == 4) board = board + deck.deal(1) },
                onNextHand = { dealNewHand() }
            )
        }
    }
}

@Composable
private fun TableWithSeats(
    hero: List<Card>,
    board: List<Card>,
    cardAlpha: List<Float>,
    onDealFlop: () -> Unit,
    onDealTurn: () -> Unit,
    onDealRiver: () -> Unit,
    onNextHand: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        val tableWidth = maxWidth
        val tableHeight = tableWidth / 1.67f // SVG aspect ratio 400:240

        Box(
            modifier = Modifier
                .size(tableWidth, tableHeight)
                .align(Alignment.Center)
        ) {
            // SVG Poker Table Background
            Image(
                painter = painterResource(id = R.drawable.poker_table),
                contentDescription = "Poker Table",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            // Content overlay
            Box(modifier = Modifier.fillMaxSize()) {
                // Center: pot + community cards
                Column(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Pot: $${(board.size + hero.size) * 10}", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(5) { idx ->
                            val alpha = cardAlpha.getOrNull(2 + idx) ?: 1f
                            if (idx < board.size) {
                                Box(modifier = Modifier.alpha(alpha)) { PlayingCard(board[idx]) }
                            } else {
                                PlaceholderCard(alpha = 0.25f)
                            }
                        }
                    }
                }

                // Dealer button near top-right seat
                DealerButton(modifier = Modifier.align(Alignment.TopEnd).padding(top = 32.dp, end = 48.dp))
            }
        }

        // Seats around (6 positions) - positioned relative to BoxWithConstraints
        Seat(name = "UTG", chips = 950, modifier = Modifier.align(Alignment.TopCenter).offset(y = 16.dp))
        Seat(name = "MP", chips = 1200, modifier = Modifier.align(Alignment.TopStart).offset(x = 24.dp, y = 48.dp))
        Seat(name = "CO", chips = 1400, modifier = Modifier.align(Alignment.CenterStart).offset(x = 8.dp))
        Seat(name = "BTN", chips = 1700, modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-16).dp))
        Seat(name = "SB", chips = 800, modifier = Modifier.align(Alignment.CenterEnd).offset(x = (-8).dp))
        Seat(name = "BB", chips = 1100, modifier = Modifier.align(Alignment.TopEnd).offset(x = (-24).dp, y = 48.dp))

        // Hero hole cards + actions
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val alpha0 = cardAlpha.getOrNull(0) ?: 1f
                val alpha1 = cardAlpha.getOrNull(1) ?: 1f
                if (hero.size >= 2) {
                    Box(modifier = Modifier.alpha(alpha0)) { PlayingCard(hero[0]) }
                    Box(modifier = Modifier.alpha(alpha1)) { PlayingCard(hero[1]) }
                } else {
                    HoleCards(cards = hero)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton("Flop", onClick = onDealFlop, color = Color(0xFF27AE60), modifier = Modifier.weight(1f))
                ActionButton("Turn", onClick = onDealTurn, color = Color(0xFF3498DB), modifier = Modifier.weight(1f))
                ActionButton("River", onClick = onDealRiver, color = Color(0xFF9B59B6), modifier = Modifier.weight(1f))
                ActionButton("Next", onClick = onNextHand, color = Color(0xFFE67E22), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Seat(name: String, chips: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111).copy(alpha = 0.80f)),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4FC3F7))
            )
            Column(Modifier.weight(1f)) {
                Text(name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text("$${chips}", color = Color(0xFFFFD700), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun DealerButton(modifier: Modifier = Modifier, size: Dp = 24.dp) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White)
            .border(2.dp, Color.Black, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("D", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PlaceholderCard(alpha: Float = 0.25f) {
    Box(
        modifier = Modifier
            .size(width = 56.dp, height = 80.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White.copy(alpha = alpha))
            .border(1.dp, Color.LightGray.copy(alpha = alpha), RoundedCornerShape(6.dp))
    ) {}
}
