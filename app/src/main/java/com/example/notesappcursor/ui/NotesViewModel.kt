package com.example.notesappcursor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesappcursor.data.Note
import com.example.notesappcursor.data.NoteRepository
import com.example.notesappcursor.data.ContentModerationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
    private val repository: NoteRepository
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val moderationService = ContentModerationService()
    private val _contentWarning = MutableStateFlow<String?>(null)
    val contentWarning = _contentWarning.asStateFlow()

    val notesState = combine(
        repository.getAllNotes(),
        searchQuery
    ) { notes, query ->
        if (query.isBlank()) {
            notes
        } else {
            notes.filter { it.title.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun checkContentRealTime(title: String, content: String) {
        viewModelScope.launch {
            val combinedText = "$title $content"
            val (isOffensive, message) = moderationService.checkContent(combinedText)
            _contentWarning.value = if (isOffensive) {
                message ?: "Warning: This note contains inappropriate content."
            } else {
                null
            }
        }
    }

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            val (isOffensive, _) = moderationService.checkContent("$title $content")
            if (!isOffensive) {
                repository.insertNote(
                    Note(
                        title = title,
                        content = content
                    )
                )
            }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            val (isOffensive, _) = moderationService.checkContent("${note.title} ${note.content}")
            if (!isOffensive) {
                repository.updateNote(note)
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun togglePinNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }
} 