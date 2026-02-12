package com.vishalpvijayan.theslate.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishalpvijayan.theslate.domain.model.ChecklistItem
import com.vishalpvijayan.theslate.domain.model.Note
import com.vishalpvijayan.theslate.domain.model.TableCell
import com.vishalpvijayan.theslate.domain.model.UserSession
import com.vishalpvijayan.theslate.domain.usecase.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class NoteMode { CREATE, VIEW, EDIT }

data class DashboardUiState(
    val session: UserSession = UserSession(),
    val searchQuery: String = "",
    val notes: List<Note> = emptyList(),
    val visibleNotes: List<Note> = emptyList(),
    val page: Int = 1,
    val hasMore: Boolean = false
)

@HiltViewModel
class AppEntryViewModel @Inject constructor(useCases: NoteUseCases) : ViewModel() {
    val session = useCases.observeSession().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserSession())
}

@HiltViewModel
class LoginViewModel @Inject constructor(private val useCases: NoteUseCases) : ViewModel() {
    fun loginAsDemo() {
        viewModelScope.launch {
            useCases.saveSession(UserSession("local_user", "Demo User", "demo@theslate.app", "", true))
        }
    }

    fun onGoogleSignedIn(userName: String, email: String, photoUrl: String, id: String) {
        viewModelScope.launch {
            useCases.saveSession(
                UserSession(
                    userId = id.ifBlank { "google_user" },
                    userName = userName,
                    email = email,
                    photoUrl = photoUrl,
                    isLoggedIn = true
                )
            )
        }
    }
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val useCases: NoteUseCases
) : ViewModel() {
    private val pageSize = 10
    private val query = MutableStateFlow("")
    private val page = MutableStateFlow(1)
    val session = useCases.observeSession().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserSession())

    val uiState: StateFlow<DashboardUiState> = combine(session, query, page, useCases.observeNotes("local_user")) { s, q, p, notes ->
        val filtered = if (q.isBlank()) notes else notes.filter {
            it.title.contains(q, true) ||
                it.description.contains(q, true) ||
                it.tags.any { tag -> tag.contains(q, true) }
        }
        val visible = filtered.take(p * pageSize)
        DashboardUiState(
            session = s,
            searchQuery = q,
            notes = filtered,
            visibleNotes = visible,
            page = p,
            hasMore = visible.size < filtered.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    fun onSearch(text: String) {
        query.value = text
        page.value = 1
    }

    fun loadNextPage() {
        page.value += 1
    }

    fun signOut() {
        viewModelScope.launch { useCases.signOut() }
    }
}

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val useCases: NoteUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val noteId: String? = savedStateHandle["noteId"]

    private val _note = MutableStateFlow(Note(noteId = noteId ?: UUID.randomUUID().toString()))
    val note: StateFlow<Note> = _note

    private val _mode = MutableStateFlow(if (noteId == null) NoteMode.CREATE else NoteMode.VIEW)
    val mode: StateFlow<NoteMode> = _mode

    init {
        if (noteId != null) {
            viewModelScope.launch {
                useCases.getNote(noteId)?.let { _note.value = it }
            }
        }
    }

    fun setMode(mode: NoteMode) { _mode.value = mode }
    fun updateTitle(value: String) { _note.value = _note.value.copy(title = value, updatedAt = System.currentTimeMillis(), isSynced = false) }
    fun updateDescription(value: String) { _note.value = _note.value.copy(description = value, updatedAt = System.currentTimeMillis(), isSynced = false) }

    fun addImage(path: String) {
        val existing = _note.value.imageAttachments
        if (existing.size < 5) _note.value = _note.value.copy(imageAttachments = existing + path, isSynced = false)
    }

    fun addAudio(path: String) {
        val existing = _note.value.audioAttachments
        if (existing.size < 2) _note.value = _note.value.copy(audioAttachments = existing + path, isSynced = false)
    }

    fun setDrawing(path: String) { _note.value = _note.value.copy(drawingImagePath = path, isSynced = false) }
    fun addChecklist(text: String) { _note.value = _note.value.copy(checklistItems = _note.value.checklistItems + ChecklistItem(text, false), isSynced = false) }
    fun toggleChecklist(index: Int) {
        _note.value = _note.value.copy(checklistItems = _note.value.checklistItems.mapIndexed { i, item -> if (i == index) item.copy(isChecked = !item.isChecked) else item })
    }

    fun addTableRow(columns: Int = 2) {
        val row = (_note.value.tableData.maxOfOrNull { it.row } ?: -1) + 1
        val additions = (0 until columns).map { column -> TableCell(row, column, "") }
        _note.value = _note.value.copy(tableData = _note.value.tableData + additions, isSynced = false)
    }

    fun updateCell(row: Int, column: Int, value: String) {
        val cells = _note.value.tableData.toMutableList()
        val idx = cells.indexOfFirst { it.row == row && it.column == column }
        if (idx >= 0) cells[idx] = cells[idx].copy(value = value)
        _note.value = _note.value.copy(tableData = cells, isSynced = false)
    }

    fun setAlarm(timeMillis: Long?) { _note.value = _note.value.copy(alarmTime = timeMillis, isSynced = false) }


    fun addTag(value: String) {
        val normalized = value.trim()
        if (normalized.isBlank()) return
        if (_note.value.tags.any { it.equals(normalized, ignoreCase = true) }) return
        _note.value = _note.value.copy(tags = _note.value.tags + normalized, isSynced = false)
    }

    fun removeTag(tag: String) {
        _note.value = _note.value.copy(
            tags = _note.value.tags.filterNot { it.equals(tag, ignoreCase = true) },
            isSynced = false
        )
    }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            useCases.upsertNote(_note.value.copy(userId = "local_user"))
            _mode.value = NoteMode.VIEW
            onDone()
        }
    }

    fun delete(onDone: () -> Unit) {
        viewModelScope.launch {
            useCases.deleteNote(_note.value.noteId)
            onDone()
        }
    }
}
