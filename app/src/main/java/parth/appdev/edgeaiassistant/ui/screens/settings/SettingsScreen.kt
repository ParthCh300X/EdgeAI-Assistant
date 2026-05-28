package parth.appdev.edgeaiassistant.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state  = viewModel.state.collectAsState().value
    val scroll = rememberScrollState()

    // Use theme colors so this screen works in both light and dark
    val bg      = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val primary = Color(0xFF6366F1)
    val onBg    = MaterialTheme.colorScheme.onBackground
    val onSurf  = MaterialTheme.colorScheme.onSurface

    var showClearNotesDialog     by remember { mutableStateOf(false) }
    var showClearAnalyticsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .verticalScroll(scroll)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = onBg
        )

        Spacer(Modifier.height(8.dp))

        // ── Appearance section ────────────────────────────────────────────
        SectionLabel("Appearance", onBg)

        SettingCard(surface) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Follow System Theme", color = onSurf,
                        style = MaterialTheme.typography.bodyLarge)
                    Text("Auto dark/light from phone settings",
                        color = onSurf.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall)
                }
                Switch(
                    checked         = state.useSystemTheme,
                    onCheckedChange = viewModel::toggleSystemTheme,
                    colors          = SwitchDefaults.colors(checkedThumbColor = primary)
                )
            }
        }

        // Manual toggle — only visible when system theme is OFF
        if (!state.useSystemTheme) {
            SettingCard(surface) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Dark Mode", color = onSurf,
                            style = MaterialTheme.typography.bodyLarge)
                        Text(if (state.isDarkMode) "Currently dark" else "Currently light",
                            color = onSurf.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(
                        checked         = state.isDarkMode,
                        onCheckedChange = viewModel::toggleDarkMode,
                        colors          = SwitchDefaults.colors(checkedThumbColor = primary)
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // ── Voice section ─────────────────────────────────────────────────
        SectionLabel("Voice", onBg)

        SettingCard(surface) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Voice Input", color = onSurf,
                        style = MaterialTheme.typography.bodyLarge)
                    Text("Enable microphone commands",
                        color = onSurf.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall)
                }
                Switch(
                    checked         = state.voiceEnabled,
                    onCheckedChange = viewModel::toggleVoice,
                    colors          = SwitchDefaults.colors(checkedThumbColor = primary)
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // ── Permissions section ───────────────────────────────────────────
        if (!state.canScheduleExactAlarms) {
            SectionLabel("Permissions", onBg)
            SettingCard(Color(0xFF7F1D1D)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Exact Alarms", color = Color.White,
                            style = MaterialTheme.typography.bodyLarge)
                        Text("Required for alarms to fire on time",
                            color = Color(0xFFFCA5A5),
                            style = MaterialTheme.typography.bodySmall)
                    }
                    TextButton(onClick = viewModel::openExactAlarmSettings) {
                        Text("Enable", color = Color(0xFFFCA5A5))
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }

        // ── Data section ──────────────────────────────────────────────────
        SectionLabel("Data", onBg)

        SettingCard(surface) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Clear Notes", color = onSurf,
                        style = MaterialTheme.typography.bodyLarge)
                    Text("Delete all saved notes",
                        color = onSurf.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall)
                }
                TextButton(onClick = { showClearNotesDialog = true }) {
                    Text("Clear", color = Color(0xFFEF4444))
                }
            }
        }

        SettingCard(surface) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Clear Analytics", color = onSurf,
                        style = MaterialTheme.typography.bodyLarge)
                    Text("Reset all usage data",
                        color = onSurf.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall)
                }
                TextButton(onClick = { showClearAnalyticsDialog = true }) {
                    Text("Clear", color = Color(0xFFEF4444))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── About ─────────────────────────────────────────────────────────
        Text("Edge AI Assistant v1.0.0",
            color = onSurf.copy(alpha = 0.4f),
            style = MaterialTheme.typography.bodySmall)
        Text("On-device AI · No data leaves your phone",
            color = onSurf.copy(alpha = 0.4f),
            style = MaterialTheme.typography.bodySmall)
    }

    if (showClearNotesDialog) {
        ConfirmDialog(
            title     = "Clear all notes?",
            message   = "This cannot be undone.",
            onConfirm = { viewModel.clearNotes(); showClearNotesDialog = false },
            onDismiss = { showClearNotesDialog = false }
        )
    }
    if (showClearAnalyticsDialog) {
        ConfirmDialog(
            title     = "Clear analytics?",
            message   = "All usage data will be reset.",
            onConfirm = { viewModel.clearAnalytics(); showClearAnalyticsDialog = false },
            onDismiss = { showClearAnalyticsDialog = false }
        )
    }
}

@Composable
fun SectionLabel(text: String, color: Color) {
    Text(
        text     = text.uppercase(),
        color    = color.copy(alpha = 0.5f),
        style    = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
    )
}

@Composable
fun SettingCard(color: Color, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = color)
    ) {
        Box(Modifier.padding(16.dp)) { content() }
    }
}

@Composable
fun ConfirmDialog(
    title     : String,
    message   : String,
    onConfirm : () -> Unit,
    onDismiss : () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title            = { Text(title) },
        text             = { Text(message) },
        confirmButton    = {
            TextButton(onClick = onConfirm) {
                Text("Confirm", color = Color(0xFFEF4444))
            }
        },
        dismissButton    = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}