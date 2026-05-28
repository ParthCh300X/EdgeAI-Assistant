package parth.appdev.edgeaiassistant.ui.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import parth.appdev.edgeaiassistant.data.local.entity.NoteEntity
import parth.appdev.edgeaiassistant.data.repository.NoteRepository
import javax.inject.Inject

data class NotesUiState(
    val notes: List<NoteEntity> = emptyList(),
    val searchQuery: String = ""
)

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _notes = repository.getAllNotesAsFlow()

    val state: StateFlow<NotesUiState> = combine(_notes, _query) { notes, query ->
        val filtered = if (query.isBlank()) notes
        else notes.filter { it.content.contains(query, ignoreCase = true) }
        NotesUiState(notes = filtered, searchQuery = query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotesUiState())

    fun onSearchChanged(q: String) { _query.value = q }

    fun deleteNote(id: Int) {
        viewModelScope.launch { repository.deleteNote(id) }
    }
}