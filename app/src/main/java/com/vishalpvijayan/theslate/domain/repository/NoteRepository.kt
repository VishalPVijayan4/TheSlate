package com.vishalpvijayan.theslate.domain.repository

import com.vishalpvijayan.theslate.domain.model.Note
import com.vishalpvijayan.theslate.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun observeNotes(userId: String): Flow<List<Note>>
    suspend fun getNote(noteId: String): Note?
    suspend fun upsert(note: Note)
    suspend fun delete(noteId: String)
    suspend fun syncPendingNotes()
    suspend fun saveSession(session: UserSession)
    fun observeSession(): Flow<UserSession>
    suspend fun signOut()
    suspend fun rescheduleAlarms()
}
