package parth.appdev.edgeaiassistant.ui.state

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

data class HomeUiState(
    val inputText: String = "",
    val messages: List<ChatMessage> = emptyList(),

    val isTyping: Boolean = false,     // 🔥 REQUIRED for typing animation

    val isListening: Boolean = false,
    val partialText: String = ""
)