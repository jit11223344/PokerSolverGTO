package com.example.pokersolverGTO.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokersolverGTO.model.Card as PokerCard
import com.example.pokersolverGTO.model.Suit

@Composable
fun PlayingCard(card: PokerCard, modifier: Modifier = Modifier, faceUp: Boolean = true) {
    val rotation by animateFloatAsState(targetValue = if (faceUp) 0f else 180f, label = "flip")
    val isRed = card.isRed()
    Box(
        modifier = modifier
            .size(width = 56.dp, height = 80.dp)
            .graphicsLayer { rotationY = rotation }
            .background(Color.White, RoundedCornerShape(6.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${card.getDisplayString()} ${card.getSuitSymbol()}",
            color = if (isRed) Color(0xFFEA4335) else Color(0xFF202124),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun HoleCards(cards: List<PokerCard>, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        cards.forEach { PlayingCard(card = it) }
    }
}

@Composable
fun CommunityCards(cards: List<PokerCard>, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        cards.forEach { PlayingCard(card = it) }
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(48.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(10.dp),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = text, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
