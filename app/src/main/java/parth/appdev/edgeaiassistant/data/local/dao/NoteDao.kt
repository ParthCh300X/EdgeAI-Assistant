package parth.appdev.edgeaiassistant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import parth.appdev.edgeaiassistant.data.local.entity.NoteEntity

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(note: NoteEntity)

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    suspend fun getAll(): List<NoteEntity>
}