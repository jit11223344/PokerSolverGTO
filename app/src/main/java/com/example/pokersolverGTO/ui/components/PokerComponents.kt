package com.example.pokersolverGTO.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChipStack(amount: Int, modifier: Modifier = Modifier) {
    val animated by animateIntAsState(targetValue = amount, label = "chips")
    Row(modifier = modifier) {
        Text(text = "$${animated}", color = Color(0xFFFFD700), fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FeltTableHeader(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF0B5F1D), Color(0xFF0A4D18))
                ),
                RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
    }
}

