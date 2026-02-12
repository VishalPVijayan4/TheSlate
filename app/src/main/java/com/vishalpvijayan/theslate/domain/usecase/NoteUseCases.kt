package com.vishalpvijayan.theslate.domain.usecase

import com.vishalpvijayan.theslate.domain.model.Note
import com.vishalpvijayan.theslate.domain.model.UserSession
import com.vishalpvijayan.theslate.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class NoteUseCases @Inject constructor(
    val observeNotes: ObserveNotes,
    val getNote: GetNote,
    val upsertNote: UpsertNote,
    val deleteNote: DeleteNote,
    val observeSession: ObserveSession,
    val saveSession: SaveSession,
    val signOut: SignOut
)

class ObserveNotes @Inject constructor(private val repo: NoteRepository) {
    operator fun invoke(userId: String): Flow<List<Note>> = repo.observeNotes(userId)
}
class GetNote @Inject constructor(private val repo: NoteRepository) { suspend operator fun invoke(noteId: String) = repo.getNote(noteId) }
class UpsertNote @Inject constructor(private val repo: NoteRepository) { suspend operator fun invoke(note: Note) = repo.upsert(note) }
class DeleteNote @Inject constructor(private val repo: NoteRepository) { suspend operator fun invoke(noteId: String) = repo.delete(noteId) }
class ObserveSession @Inject constructor(private val repo: NoteRepository) { operator fun invoke(): Flow<UserSession> = repo.observeSession() }
class SaveSession @Inject constructor(private val repo: NoteRepository) { suspend operator fun invoke(session: UserSession) = repo.saveSession(session) }
class SignOut @Inject constructor(private val repo: NoteRepository) { suspend operator fun invoke() = repo.signOut() }
