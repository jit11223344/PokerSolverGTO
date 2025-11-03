package com.example.pokersolverGTO.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.pokersolverGTO.navigation.Screen

data class NavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    closeDrawer: () -> Unit
) {
    val items = listOf(
        NavigationItem(Screen.TrainerHome.route, "Home", Icons.Filled.Home),
        NavigationItem(Screen.EquityCalculator.route, "Equity Calculator", Icons.Filled.Star),
        NavigationItem(Screen.Preflop.route, "Preflop Training", Icons.Filled.PlayArrow),
        NavigationItem(Screen.BestHand.route, "Hand Evaluator", Icons.Filled.Build),
        NavigationItem(Screen.Settings.route, "Settings", Icons.Filled.Settings)
    )

    ModalDrawerSheet {
        Spacer(Modifier.height(12.dp))
        items.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    onNavigate(item.route)
                    closeDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}
