package parth.appdev.edgeaiassistant.data.local.dao

import androidx.room.*
import parth.appdev.edgeaiassistant.data.local.entity.ChatEntity

@Dao
interface ChatDao {

    @Insert
    suspend fun insert(message: ChatEntity)

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT 50")
    suspend fun getLast50(): List<ChatEntity>

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAll()
}