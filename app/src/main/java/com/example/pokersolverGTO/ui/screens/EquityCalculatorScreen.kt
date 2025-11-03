package com.example.pokersolverGTO.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokersolverGTO.equity.models.*
import com.example.pokersolverGTO.equity.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquityCalculatorScreen(
    onBackPressed: () -> Unit,
    viewModel: EquityCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Equity Calculator") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B263B))
            )
        },
        containerColor = Color(0xFF0D1B2A)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // keep 8dp spacing between items
        ) {
            item {
                CardSelectorGrid(
                    onCardSelected = { rank, suit -> viewModel.selectCard(rank, suit) },
                    onClear = { viewModel.clearLastCard() },
                    onClearAll = { viewModel.clearAll() },
                    usedCards = state.player1Cards + state.player2Cards + state.flopCards + listOfNotNull(state.turnCard, state.riverCard)
                )
            }

            item {
                PlayerCardSlots(
                    playerName = "Player 1 (Hero)",
                    cards = state.player1Cards,
                    onCardClick = { idx -> viewModel.setCurrentSelection(if (idx == 0) CardSelection.PLAYER1_CARD1 else CardSelection.PLAYER1_CARD2) },
                    isActive = state.currentSelection == CardSelection.PLAYER1_CARD1 || state.currentSelection == CardSelection.PLAYER1_CARD2
                )
            }

            item {
                PlayerCardSlots(
                    playerName = "Player 2 (Villain)",
                    cards = state.player2Cards,
                    onCardClick = { idx -> viewModel.setCurrentSelection(if (idx == 0) CardSelection.PLAYER2_CARD1 else CardSelection.PLAYER2_CARD2) },
                    isActive = state.currentSelection == CardSelection.PLAYER2_CARD1 || state.currentSelection == CardSelection.PLAYER2_CARD2
                )
            }

            item {
                CommunityCardSlots(
                    flopCards = state.flopCards,
                    turnCard = state.turnCard,
                    riverCard = state.riverCard,
                    currentSelection = state.currentSelection,
                    onCardClick = { sel -> viewModel.setCurrentSelection(sel) }
                )
            }

            // Calculate Button
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.calculateEquity() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isCalculating && state.player1Cards.size == 2 && state.player2Cards.size == 2,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60))
                    ) {
                        if (state.isCalculating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(if (state.isCalculating) "Calculating..." else "Calculate Equity", fontWeight = FontWeight.Bold)
                    }

                    // Debug / status row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (state.isCalculating) {
                            Text(text = "Running calculation...", color = Color.LightGray)
                        } else if (state.calculationDone) {
                            Text(text = "Calculation done", color = Color.Green)
                        }

                        state.errorMessage?.let { err ->
                            Text(text = err, color = Color(0xFFE74C3C))
                        }
                    }
                }
            }

            // Results: show when calculationDone or when percents > 0
            item {
                if (state.calculationDone || state.player1WinPercent > 0f || state.player2WinPercent > 0f) {
                    EquityResults(
                        player1Percent = state.player1WinPercent,
                        player2Percent = state.player2WinPercent,
                        tiePercent = state.tiePercent
                    )
                }
            }

            state.errorMessage?.let { err ->
                item {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE74C3C).copy(alpha = 0.12f))) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = err, color = Color(0xFFE74C3C), modifier = Modifier.weight(1f))
                            TextButton(onClick = { viewModel.clearError() }) { Text("Dismiss") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardSelectorGrid(
    onCardSelected: (Rank, Suit) -> Unit,
    onClear: () -> Unit,
    onClearAll: () -> Unit,
    usedCards: List<Card>
) {
    var selectedRank by remember { mutableStateOf<Rank?>(null) }

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Select Cards", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Rank.entries.forEach { rank ->
                    RankButton(rank = rank, isSelected = selectedRank == rank, onClick = { selectedRank = if (selectedRank == rank) null else rank }, modifier = Modifier.weight(1f))
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Suit.entries.forEach { suit ->
                    val isUsed = selectedRank?.let { rank -> Card(rank, suit) in usedCards } ?: false
                    SuitButton(suit = suit, onClick = { selectedRank?.let { rank -> if (!isUsed) { onCardSelected(rank, suit); selectedRank = null } } }, enabled = selectedRank != null, isUsed = isUsed, modifier = Modifier.weight(1f))
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f)) { Text("CLEAR") }
                OutlinedButton(onClick = onClearAll, modifier = Modifier.weight(1f)) { Text("CLEAR ALL") }
            }
        }
    }
}

@Composable
private fun RankButton(rank: Rank, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.aspectRatio(1f).background(color = if (isSelected) Color(0xFF3498DB) else Color(0xFF2C3E50), shape = RoundedCornerShape(4.dp)).border(width = 1.dp, color = if (isSelected) Color(0xFF3498DB) else Color(0xFF34495E), shape = RoundedCornerShape(4.dp)).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(text = rank.symbol, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SuitButton(suit: Suit, onClick: () -> Unit, enabled: Boolean = true, isUsed: Boolean = false, modifier: Modifier = Modifier) {
    val bg = when {
        isUsed -> Color(0xFF7F8C8D)
        enabled -> Color(0xFF2C3E50)
        else -> Color(0xFF1B263B)
    }
    Box(modifier = modifier.aspectRatio(1f).background(color = bg, shape = RoundedCornerShape(4.dp)).border(width = 1.dp, color = if (isUsed) Color.Gray else Color.White, shape = RoundedCornerShape(4.dp)).clickable(enabled = enabled && !isUsed, onClick = onClick), contentAlignment = Alignment.Center) {
        Text(text = suit.symbol, color = if (suit == Suit.HEART || suit == Suit.DIAMOND) Color(0xFFE74C3C) else Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PlayerCardSlots(playerName: String, cards: List<Card>, onCardClick: (Int) -> Unit, isActive: Boolean) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (isActive) Color(0xFF2C3E50) else Color(0xFF1B263B)), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = playerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(2) { idx ->
                    CardSlot(card = cards.getOrNull(idx), onClick = { onCardClick(idx) }, isActive = isActive && (idx == 0 || idx == 1), modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CommunityCardSlots(flopCards: List<Card>, turnCard: Card?, riverCard: Card?, currentSelection: CardSelection, onCardClick: (CardSelection) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Community Cards", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { i ->
                    val active = when (i) { 0 -> currentSelection == CardSelection.FLOP_CARD1; 1 -> currentSelection == CardSelection.FLOP_CARD2; else -> currentSelection == CardSelection.FLOP_CARD3 }
                    CardSlot(card = flopCards.getOrNull(i), onClick = { onCardClick(when (i) { 0 -> CardSelection.FLOP_CARD1; 1 -> CardSelection.FLOP_CARD2; else -> CardSelection.FLOP_CARD3 }) }, isActive = active, modifier = Modifier.weight(1f))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CardSlot(card = turnCard, onClick = { onCardClick(CardSelection.TURN) }, isActive = currentSelection == CardSelection.TURN, modifier = Modifier.weight(1f))
                CardSlot(card = riverCard, onClick = { onCardClick(CardSelection.RIVER) }, isActive = currentSelection == CardSelection.RIVER, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CardSlot(card: Card?, onClick: () -> Unit, isActive: Boolean = false, modifier: Modifier = Modifier) {
    // changed: fixed size to 60 x 85 dp, font size 20sp, spacing kept 8dp
    Box(
        modifier = modifier
            .size(width = 60.dp, height = 85.dp)
            .background(
                color = if (isActive) Color(0xFF3498DB).copy(alpha = 0.25f) else Color(0xFF2C3E50),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = if (isActive) Color(0xFF3498DB) else Color(0xFF34495E),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (card != null) {
            Text(
                text = card.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (card.suit == Suit.HEART || card.suit == Suit.DIAMOND) Color(0xFFE74C3C) else Color.White
            )
        } else {
            Text(text = "?", color = Color.Gray, fontSize = 20.sp)
        }
    }
}

@Composable
private fun EquityResults(player1Percent: Float, player2Percent: Float, tiePercent: Float) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Equity Results", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            EquityBar("Player 1", player1Percent, Color(0xFF27AE60))
            EquityBar("Player 2", player2Percent, Color(0xFFE74C3C))
            if (tiePercent > 0.1f) EquityBar("Tie", tiePercent, Color(0xFF95A5A6))
        }
    }
}

@Composable
private fun EquityBar(label: String, percent: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, fontSize = 14.sp, color = Color.White)
            Text(text = "%.2f%%".format(percent), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(color = Color(0xFF2C3E50), shape = RoundedCornerShape(4.dp))) {
            Box(modifier = Modifier.fillMaxWidth(percent / 100f).fillMaxHeight().background(color = color, shape = RoundedCornerShape(4.dp)))
        }
    }
}
