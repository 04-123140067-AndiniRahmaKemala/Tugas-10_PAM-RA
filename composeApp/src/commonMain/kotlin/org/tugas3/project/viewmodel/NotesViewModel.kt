package org.tugas3.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.tugas3.project.NetworkMonitor
import org.tugas3.project.data.NoteRepository
import org.tugas3.project.data.SettingsManager
import org.tugas3.project.db.NoteEntity

sealed class NotesUiState {
    object Loading : NotesUiState()
    object Empty : NotesUiState()
    data class Success(
        val notes: List<NoteEntity>,
        val isConnected: Boolean = true
    ) : NotesUiState()
}

class NotesViewModel(
    private val repository: NoteRepository,
    private val settingsManager: SettingsManager,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val isConnected = networkMonitor.isConnected
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val uiState: StateFlow<NotesUiState> = combine(
        _searchQuery.debounce(300),
        settingsManager.sortOrder,
        isConnected
    ) { query, sortOrder, connected ->
        Triple(query, sortOrder, connected)
    }.flatMapLatest { (query, sortOrder, connected) ->
        val flow = if (query.isEmpty()) {
            repository.getAllNotes()
        } else {
            repository.searchNotes(query)
        }
        
        flow.map { notes ->
            val sortedNotes = when (sortOrder) {
                "date_asc" -> notes.sortedBy { it.date }
                "title_asc" -> notes.sortedBy { it.title }
                "title_desc" -> notes.sortedByDescending { it.title }
                else -> notes.sortedByDescending { it.date } // date_desc
            }
            if (sortedNotes.isEmpty() && query.isEmpty()) {
                NotesUiState.Empty 
            } else {
                NotesUiState.Success(sortedNotes, isConnected = connected)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotesUiState.Loading)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun addNote(title: String, content: String, category: String = "General") {
        viewModelScope.launch {
            repository.insertNote(
                title = title,
                content = content,
                date = getCurrentDate(),
                category = category
            )
        }
    }

    fun updateNote(id: Long, title: String, content: String, category: String = "General") {
        viewModelScope.launch {
            repository.updateNote(
                id = id,
                title = title,
                content = content,
                date = getCurrentDate(),
                category = category
            )
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            val note = repository.getNoteById(id)
            if (note != null) {
                repository.updateFavorite(id, !note.isFavorite)
            }
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }

    private fun getCurrentDate(): String {
        return "Oct 25, 2023"
    }
}
