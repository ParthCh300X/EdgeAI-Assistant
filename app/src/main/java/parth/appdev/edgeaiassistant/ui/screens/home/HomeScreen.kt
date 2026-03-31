package parth.appdev.edgeaiassistant.ui.screens.home

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
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import parth.appdev.edgeaiassistant.engine.voice.VoiceManager
import parth.appdev.edgeaiassistant.ui.state.ChatMessage
import parth.appdev.edgeaiassistant.ui.state.HomeUiState

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    val voiceManager = remember {
        VoiceManager(context, object : VoiceManager.VoiceCallback {
            override fun onPartial(text: String) {
                viewModel.onVoicePartial(text)
            }

            override fun onFinal(text: String) {
                viewModel.onVoiceFinal(text)
            }

            override fun onError() {
                viewModel.stopListening()
            }
        })
    }

    val bgColor = Color(0xFF0B1220)
    val userBubble = Color(0xFF6366F1)
    val aiBubble = Color(0xFF1E293B)

    // 🔥 AUTO SCROLL
    LaunchedEffect(state.messages.size, state.isTyping) {
        delay(100)
        listState.animateScrollToItem(0)
    }

    Scaffold(
        containerColor = bgColor,

        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Edge AI",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )

                TextButton(
                    onClick = { navController.navigate("analytics") }
                ) {
                    Text("Analytics", color = userBubble)
                }
            }
        },

        bottomBar = {
            InputSection(state, viewModel, voiceManager)
        }

    ) { padding ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            reverseLayout = true,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            items(state.messages.reversed()) { message ->

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 2 }
                ) {
                    MessageBubble(
                        message = message,
                        userColor = userBubble,
                        aiColor = aiBubble
                    )
                }
            }

            // 🔥 TYPING BUBBLE
            if (state.isTyping) {
                item {
                    TypingBubble(aiBubble)
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    userColor: Color,
    aiColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser)
            Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (message.isUser) userColor else aiColor,
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 2.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
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

    val dots = ".".repeat(dotCount)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = aiColor,
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 2.dp,
            modifier = Modifier.widthIn(max = 100.dp)
        ) {
            Text(
                text = dots,
                color = Color.White,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun InputSection(
    state: HomeUiState,
    viewModel: HomeViewModel,
    voiceManager: VoiceManager
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF020617))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        TextField(
            value = state.inputText,
            onValueChange = { viewModel.onTextChanged(it) },
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            placeholder = {
                Text("Ask anything...", color = Color.Gray)
            },
            shape = RoundedCornerShape(14.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E293B),
                unfocusedContainerColor = Color(0xFF1E293B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = { viewModel.onSend() },
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6366F1)
            )
        ) {
            Text("Go")
        }

        IconButton(
            onClick = {
                if (!state.isListening) {
                    viewModel.startListening()
                    voiceManager.start()
                } else {
                    viewModel.stopListening()
                    voiceManager.stop()
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Mic",
                tint = Color.White
            )
        }
    }
}