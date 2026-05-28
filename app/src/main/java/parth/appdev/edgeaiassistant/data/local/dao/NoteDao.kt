package parth.appdev.edgeaiassistant.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import parth.appdev.edgeaiassistant.data.local.entity.NoteEntity

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(note: NoteEntity)

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    suspend fun getAll(): List<NoteEntity>

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllAsFlow(): Flow<List<NoteEntity>>

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM notes")
    suspend fun deleteAll()
}