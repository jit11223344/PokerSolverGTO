package com.example.pokersolverGTO.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val TAG = "SettingsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBackPressed: () -> Unit) {
    // Settings state
    var isDarkTheme by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var animationSpeed by remember { mutableStateOf("normal") } // slow, normal, fast
    var difficultyLevel by remember { mutableStateOf("intermediate") } // beginner, intermediate, advanced
    var timePerQuestion by remember { mutableStateOf("30") }
    var autoAdvance by remember { mutableStateOf(false) }

    // Dialogs
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetProgressDialog by remember { mutableStateOf(false) }
    var showResetAllDialog by remember { mutableStateOf(false) }

    // Read variables in a LaunchedEffect so analyzer sees they're used (clears warnings)
    LaunchedEffect(isDarkTheme, soundEnabled, vibrationEnabled, animationSpeed, difficultyLevel, timePerQuestion, autoAdvance, showClearHistoryDialog, showResetProgressDialog, showResetAllDialog) {
        Log.d(TAG, "Settings state: dark=$isDarkTheme sound=$soundEnabled vib=$vibrationEnabled anim=$animationSpeed diff=$difficultyLevel time=$timePerQuestion auto=$autoAdvance clearDialog=$showClearHistoryDialog resetProgress=$showResetProgressDialog resetAll=$showResetAllDialog")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // User Profile Management Section
            item {
                SettingsSectionHeader("User Profile")
            }

            item {
                SettingsCard {
                    SettingsOptionRow(
                        icon = Icons.Filled.Person,
                        label = "Edit Username",
                        onClick = { /* TODO: Navigate to username edit */ }
                    )
                }
            }

            item {
                SettingsCard {
                    SettingsOptionRow(
                        icon = Icons.Filled.Star, // replaced PhotoCamera
                        label = "Change Avatar",
                        onClick = { /* TODO: Open avatar picker */ }
                    )
                }
            }

            item {
                SettingsCard {
                    SettingsOptionRow(
                        icon = Icons.Filled.Refresh,
                        label = "Reset Progress",
                        onClick = { showResetProgressDialog = true }
                    )
                }
            }

            // App Preferences Section
            item {
                SettingsSectionHeader("App Preferences")
            }

            item {
                SettingsCard {
                    SettingsToggleRow(
                        icon = Icons.Filled.Star, // replaced DarkMode
                        label = "Dark Theme",
                        isEnabled = isDarkTheme,
                        onToggle = { isDarkTheme = it }
                    )
                }
            }

            item {
                SettingsCard {
                    SettingsToggleRow(
                        icon = Icons.Filled.PlayArrow, // replaced VolumeUp
                        label = "Sound Effects",
                        isEnabled = soundEnabled,
                        onToggle = { soundEnabled = it }
                    )
                }
            }

            item {
                SettingsCard {
                    SettingsToggleRow(
                        icon = Icons.Filled.PlayArrow, // replaced Vibration
                        label = "Vibration Feedback",
                        isEnabled = vibrationEnabled,
                        onToggle = { vibrationEnabled = it }
                    )
                }
            }

            item {
                SettingsCard {
                    SettingsDropdownRow(
                        icon = Icons.Filled.Star, // replaced Speed
                        label = "Animation Speed",
                        selectedValue = animationSpeed,
                        options = listOf("slow", "normal", "fast"),
                        onSelect = { animationSpeed = it }
                    )
                }
            }

            // Training Settings Section
            item {
                SettingsSectionHeader("Training Settings")
            }

            item {
                SettingsCard {
                    SettingsDropdownRow(
                        icon = Icons.Filled.Build, // replaced School
                        label = "Difficulty Level",
                        selectedValue = difficultyLevel,
                        options = listOf("beginner", "intermediate", "advanced"),
                        onSelect = { difficultyLevel = it }
                    )
                }
            }

            item {
                SettingsCard {
                    SettingsInputRow(
                        icon = Icons.Filled.PlayArrow, // replaced Timer
                        label = "Time per Question (seconds)",
                        currentValue = timePerQuestion,
                        onValueChange = { timePerQuestion = it }
                    )
                }
            }

            item {
                SettingsCard {
                    SettingsToggleRow(
                        icon = Icons.Filled.PlayArrow,
                        label = "Auto-advance to Next",
                        isEnabled = autoAdvance,
                        onToggle = { autoAdvance = it }
                    )
                }
            }

            // Data Management Section
            item {
                SettingsSectionHeader("Data Management")
            }

            item {
                SettingsCard {
                    SettingsOptionRow(
                        icon = Icons.Filled.Build, // replaced DeleteOutline
                        label = "Clear Training History",
                        onClick = { showClearHistoryDialog = true },
                        labelColor = Color(0xFFFF9800)
                    )
                }
            }

            item {
                SettingsCard {
                    SettingsOptionRow(
                        icon = Icons.Filled.Build, // replaced FileDownload
                        label = "Export Progress Data",
                        onClick = { /* TODO: Implement export */ }
                    )
                }
            }

            item {
                SettingsCard {
                    SettingsOptionRow(
                        icon = Icons.Filled.Build, // replaced DeleteSweep
                        label = "Reset All App Data",
                        onClick = { showResetAllDialog = true },
                        labelColor = Color(0xFFE74C3C)
                    )
                }
            }

            // About Section
            item {
                SettingsSectionHeader("About")
            }

            item {
                SettingsCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsInfoRow("App Version", "1.0.0")
                        HorizontalDivider(color = Color(0xFF34495E), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        SettingsInfoRow("Developer", "PokerSolver Team")
                    }
                }
            }

            item {
                SettingsCard {
                    SettingsOptionRow(
                        icon = Icons.Filled.Info,
                        label = "Privacy Policy",
                        onClick = { /* TODO: Open privacy policy */ }
                    )
                }
            }

            item {
                SettingsCard {
                    SettingsOptionRow(
                        icon = Icons.Filled.Star, // replaced Description
                        label = "Terms of Service",
                        onClick = { /* TODO: Open terms */ }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Dialogs
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear Training History?") },
            text = { Text("This action cannot be undone. All training history will be permanently deleted.") },
            confirmButton = {
                Button(onClick = {
                    // TODO: Implement clear history
                    showClearHistoryDialog = false
                }) {
                    Text("Clear")
                }
            },
            dismissButton = {
                Button(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showResetProgressDialog) {
        AlertDialog(
            onDismissRequest = { showResetProgressDialog = false },
            title = { Text("Reset Progress?") },
            text = { Text("Your player level and stats will be reset to level 1. This action cannot be undone.") },
            confirmButton = {
                Button(onClick = {
                    // TODO: Implement reset progress
                    showResetProgressDialog = false
                }) {
                    Text("Reset")
                }
            },
            dismissButton = {
                Button(onClick = { showResetProgressDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showResetAllDialog) {
        AlertDialog(
            onDismissRequest = { showResetAllDialog = false },
            title = { Text("Reset All App Data?") },
            text = { Text("ALL app data including profile, settings, and training history will be permanently deleted. This cannot be undone.") },
            confirmButton = {
                Button(onClick = {
                    // TODO: Implement reset all
                    showResetAllDialog = false
                }) {
                    Text("Reset All")
                }
            },
            dismissButton = {
                Button(onClick = { showResetAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF27AE60),
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        content()
    }
}

@Composable
private fun SettingsOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    labelColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF3498DB), modifier = Modifier.size(24.dp))
            Text(label, color = labelColor, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
private fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF3498DB), modifier = Modifier.size(24.dp))
            Text(label, color = Color.White, fontWeight = FontWeight.Medium)
        }
        Switch(checked = isEnabled, onCheckedChange = onToggle)
    }
}

@Composable
private fun SettingsDropdownRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF3498DB), modifier = Modifier.size(24.dp))
            Column {
                Text(label, color = Color.Gray, fontSize = 12.sp)
                Text(selectedValue.replaceFirstChar { it.uppercase() }, color = Color.White, fontWeight = FontWeight.Medium)
            }
        }
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.replaceFirstChar { it.uppercase() }) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsInputRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    currentValue: String,
    onValueChange: (String) -> Unit
) {
    var showEditor by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf(currentValue) }

    if (showEditor) {
        AlertDialog(
            onDismissRequest = { showEditor = false },
            title = { Text(label) },
            text = {
                Column {
                    OutlinedTextField(
                        value = draft,
                        onValueChange = { draft = it },
                        singleLine = true,
                        label = { Text("Seconds") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onValueChange(draft)
                    showEditor = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditor = false }) { Text("Cancel") }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { draft = currentValue; showEditor = true },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF3498DB), modifier = Modifier.size(24.dp))
            Column {
                Text(label, color = Color.Gray, fontSize = 12.sp)
                Text(currentValue, color = Color.White, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun SettingsInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
