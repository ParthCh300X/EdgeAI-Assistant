package parth.appdev.edgeaiassistant.ui.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import parth.appdev.edgeaiassistant.engine.voice.VoiceManager
import parth.appdev.edgeaiassistant.ui.state.ChatMessage
import parth.appdev.edgeaiassistant.ui.state.HomeUiState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    navController : NavController,
    viewModel     : HomeViewModel = hiltViewModel()
) {
    val state             = viewModel.state.collectAsState().value
    val context           = LocalContext.current
    val listState         = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val timeSdf           = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    val voiceManager = remember {
        VoiceManager(context, object : VoiceManager.VoiceCallback {
            override fun onPartial(text: String) { viewModel.onVoicePartial(text) }
            override fun onFinal(text: String)   { viewModel.onVoiceFinal(text) }
            override fun onError()               { viewModel.stopListening() }
        })
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.onErrorShown()
        }
    }

    LaunchedEffect(state.messages.size, state.isTyping) {
        delay(100)
        if (state.messages.isNotEmpty()) listState.animateScrollToItem(0)
    }

    val bgColor    = Color(0xFF0B1220)
    val userBubble = Color(0xFF6366F1)
    val aiBubble   = Color(0xFF1E293B)

    Scaffold(
        containerColor = bgColor,
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = "Edge AI",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text  = "On-device AI",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        bottomBar = {
            Column {
                SuggestionChips(
                    suggestions = state.suggestions,
                    onChipClick = { viewModel.onTextChanged(it) }
                )
                InputSection(state, viewModel, voiceManager)
            }
        }
    ) { padding ->
        LazyColumn(
            state               = listState,
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding),
            reverseLayout       = true,
            contentPadding      = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (state.isTyping) {
                item { TypingBubble(aiBubble) }
            }

            items(state.messages.reversed()) { message ->
                AnimatedVisibility(
                    visible = true,
                    enter   = fadeIn() + slideInVertically { it / 2 }
                ) {
                    MessageBubble(
                        message   = message,
                        userColor = userBubble,
                        aiColor   = aiBubble,
                        timeLabel = timeSdf.format(Date(message.timestamp))
                    )
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
fun MessageBubble(
    message   : ChatMessage,
    userColor : Color,
    aiColor   : Color,
    timeLabel : String
) {
    val sender = if (message.isUser) "You" else "Edge AI"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$sender said: ${message.text} at $timeLabel"
            },
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color          = if (message.isUser) userColor else aiColor,
            shape          = RoundedCornerShape(18.dp),
            tonalElevation = 2.dp,
            modifier       = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text     = message.text,
                color    = Color.White,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                style    = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text     = timeLabel,
            color    = Color.Gray,
            style    = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun TypingBubble(aiColor: Color) {
    var dotCount by remember { mutableStateOf(1) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(400)
            dotCount = (dotCount % 3) + 1
        }
    }
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color          = aiColor,
            shape          = RoundedCornerShape(18.dp),
            tonalElevation = 2.dp,
            modifier       = Modifier.widthIn(max = 80.dp)
        ) {
            Text(
                text     = ".".repeat(dotCount),
                color    = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun WaveformBars(modifier: Modifier = Modifier) {
    val primary  = Color(0xFF6366F1)
    val barCount = 5

    val heights = (0 until barCount).map { i ->
        var target by remember { mutableStateOf(0.3f + i * 0.1f) }
        val anim   by animateFloatAsState(
            targetValue   = target,
            animationSpec = tween(durationMillis = 300 + i * 60),
            label         = "waveBar$i"
        )
        LaunchedEffect(Unit) {
            while (true) {
                target = (0.2f + Math.random() * 0.8f).toFloat()
                delay(250L + i * 40L)
            }
        }
        anim
    }

    Row(
        modifier              = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        heights.forEach { h ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height((h * 28).dp)
                    .background(primary, RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
fun SuggestionChips(
    suggestions : List<String>,
    onChipClick : (String) -> Unit
) {
    if (suggestions.isEmpty()) return

    val primary = Color(0xFF6366F1)
    val haptic  = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF020617))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        suggestions
            .take(2)
            .map { if (it.length > 18) it.take(16) + "..." else it }
            .forEach { label ->
                SuggestionChip(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onChipClick(label.trimEnd('.'))
                    },
                    label   = {
                        Text(
                            text     = label,
                            style    = MaterialTheme.typography.labelSmall,
                            color    = Color.White,
                            maxLines = 1
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors   = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Color(0xFF1E293B)
                    ),
                    border   = SuggestionChipDefaults.suggestionChipBorder(
                        enabled     = true,
                        borderColor = primary
                    )
                )
            }
    }
}

@Composable
fun InputSection(
    state        : HomeUiState,
    viewModel    : HomeViewModel,
    voiceManager : VoiceManager
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF020617))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value         = state.inputText,
            onValueChange = { viewModel.onTextChanged(it) },
            modifier      = Modifier
                .weight(1f)
                .height(52.dp)
                .semantics { contentDescription = "Command input field" },
            placeholder = { Text("Ask anything...", color = Color.Gray) },
            shape       = RoundedCornerShape(14.dp),
            colors      = TextFieldDefaults.colors(
                focusedContainerColor   = Color(0xFF1E293B),
                unfocusedContainerColor = Color(0xFF1E293B),
                focusedTextColor        = Color.White,
                unfocusedTextColor      = Color.White,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(Modifier.width(8.dp))

        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.onSend()
            },
            modifier       = Modifier.semantics { contentDescription = "Send command" },
            shape          = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            colors         = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
        ) {
            Text("Go")
        }

        Box(
            modifier         = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.isListening) {
                WaveformBars(modifier = Modifier.height(32.dp))
            }

            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (!state.isListening) {
                        viewModel.startListening()
                        voiceManager.start()
                    } else {
                        viewModel.stopListening()
                        voiceManager.stop()
                    }
                }
            ) {
                if (!state.isListening) {
                    Icon(
                        imageVector        = Icons.Default.Mic,
                        contentDescription = "Start voice input",
                        tint               = Color.White
                    )
                }
            }
        }
    }
}