package parth.appdev.edgeaiassistant.data.repository

import parth.appdev.edgeaiassistant.data.local.dao.ChatDao
import parth.appdev.edgeaiassistant.data.local.entity.ChatEntity
import parth.appdev.edgeaiassistant.ui.state.ChatMessage
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val dao: ChatDao
) {
    suspend fun save(text: String, isUser: Boolean) {
        dao.insert(ChatEntity(text = text, isUser = isUser))
    }

    suspend fun loadHistory(): List<ChatMessage> {
        return dao.getLast50()
            .reversed()   // oldest first
            .map { ChatMessage(text = it.text, isUser = it.isUser, timestamp = it.timestamp) }
    }

    suspend fun clearAll() = dao.deleteAll()
}