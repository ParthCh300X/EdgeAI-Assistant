package parth.appdev.edgeaiassistant.ui.screens.onboarding

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OnboardingScreen(
    onComplete : () -> Unit,
    viewModel  : OnboardingViewModel = hiltViewModel()
) {
    var page    by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val bgColor = Color(0xFF0B1220)
    val primary = Color(0xFF6366F1)

    val micLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { page = 2 }

    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.spacedBy(24.dp)
        ) {

            when (page) {

                // ── Page 0 — Welcome ──────────────────────────────────────
                0 -> {
                    Text(text = "⚡", fontSize = 64.sp)

                    Text(
                        text      = "Edge AI Assistant",
                        color     = Color.White,
                        style     = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign  = TextAlign.Center
                    )

                    Text(
                        text      = "A fully on-device AI assistant.\n" +
                                "Set alarms, take notes, calculate,\n" +
                                "convert units — all without internet.",
                        color     = Color.Gray,
                        style     = MaterialTheme.typography.bodyLarge,
                        textAlign  = TextAlign.Center
                    )

                    OnboardingButton(primary, "Get Started") { page = 1 }
                }

                // ── Page 1 — Microphone ───────────────────────────────────
                1 -> {
                    Text(text = "🎤", fontSize = 64.sp)

                    Text(
                        text       = "Voice Commands",
                        color      = Color.White,
                        style      = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign  = TextAlign.Center
                    )

                    Text(
                        text      = "Tap the mic and speak naturally.\n" +
                                "Try: \"Set alarm for 7am\", " +
                                "\"calculate 5 times 8\", " +
                                "\"open YouTube\"",
                        color     = Color.Gray,
                        style     = MaterialTheme.typography.bodyLarge,
                        textAlign  = TextAlign.Center
                    )

                    OnboardingButton(primary, "Allow Microphone") {
                        micLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }

                    TextButton(onClick = { page = 2 }) {
                        Text("Skip", color = Color.Gray)
                    }
                }

                // ── Page 2 — Exact alarms ─────────────────────────────────
                2 -> {
                    Text(text = "⏰", fontSize = 64.sp)

                    Text(
                        text       = "Precise Alarms",
                        color      = Color.White,
                        style      = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign  = TextAlign.Center
                    )

                    Text(
                        text      = "For alarms to fire exactly on time,\n" +
                                "Edge AI needs the exact alarm permission.",
                        color     = Color.Gray,
                        style     = MaterialTheme.typography.bodyLarge,
                        textAlign  = TextAlign.Center
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        OnboardingButton(primary, "Allow Exact Alarms") {
                            context.startActivity(
                                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                            viewModel.completeOnboarding()
                            onComplete()
                        }
                        TextButton(
                            onClick = {
                                viewModel.completeOnboarding()
                                onComplete()
                            }
                        ) {
                            Text("Skip", color = Color.Gray)
                        }
                    } else {
                        OnboardingButton(primary, "Let's Go!") {
                            viewModel.completeOnboarding()
                            onComplete()
                        }
                    }
                }
            }

            // Page indicator dots
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == page) 10.dp else 6.dp)
                            .background(
                                color = if (i == page) primary else Color.Gray,
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingButton(
    color   : Color,
    text    : String,
    onClick : () -> Unit
) {
    Button(
        onClick        = onClick,
        modifier       = Modifier.fillMaxWidth(),
        shape          = RoundedCornerShape(14.dp),
        colors         = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(
            text     = text,
            modifier = Modifier.padding(vertical = 6.dp)
        )
    }
}