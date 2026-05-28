package parth.appdev.edgeaiassistant.ui.state

data class ChatMessage(
    val text      : String,
    val isUser    : Boolean,
    val timestamp : Long = System.currentTimeMillis()
)

data class HomeUiState(
    val inputText    : String          = "",
    val messages     : List<ChatMessage> = emptyList(),
    val isTyping     : Boolean         = false,
    val isListening  : Boolean         = false,
    val partialText  : String          = "",
    val errorMessage : String?         = null,
    val suggestions  : List<String>    = emptyList()
)