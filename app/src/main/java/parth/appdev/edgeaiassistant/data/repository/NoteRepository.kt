package parth.appdev.edgeaiassistant.data.repository

import kotlinx.coroutines.flow.Flow
import parth.appdev.edgeaiassistant.data.local.dao.NoteDao
import parth.appdev.edgeaiassistant.data.local.entity.NoteEntity
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val dao: NoteDao
) {
    suspend fun saveNote(content: String) = dao.insert(NoteEntity(content = content))
    suspend fun getAllNotes(): List<NoteEntity> = dao.getAll()
    fun getAllNotesAsFlow(): Flow<List<NoteEntity>> = dao.getAllAsFlow()
    suspend fun deleteNote(id: Int) = dao.deleteById(id)
    suspend fun deleteAllNotes() = dao.deleteAll()
}