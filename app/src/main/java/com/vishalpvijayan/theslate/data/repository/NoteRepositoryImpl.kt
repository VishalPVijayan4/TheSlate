package com.vishalpvijayan.theslate.data.repository

import com.vishalpvijayan.theslate.data.local.ChecklistDao
import com.vishalpvijayan.theslate.data.local.NoteDao
import com.vishalpvijayan.theslate.data.local.NoteMappers.toChecklistEntities
import com.vishalpvijayan.theslate.data.local.NoteMappers.toChecklistItems
import com.vishalpvijayan.theslate.data.local.NoteMappers.toCells
import com.vishalpvijayan.theslate.data.local.NoteMappers.toEntity
import com.vishalpvijayan.theslate.data.local.NoteMappers.toNote
import com.vishalpvijayan.theslate.data.local.NoteMappers.toTableEntities
import com.vishalpvijayan.theslate.data.local.SyncQueueDao
import com.vishalpvijayan.theslate.data.local.SyncQueueEntity
import com.vishalpvijayan.theslate.data.local.TableCellDao
import com.vishalpvijayan.theslate.domain.model.Note
import com.vishalpvijayan.theslate.domain.model.UserSession
import com.vishalpvijayan.theslate.domain.repository.NoteRepository
import com.vishalpvijayan.theslate.worker.AlarmScheduler
import com.vishalpvijayan.theslate.worker.SyncScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val checklistDao: ChecklistDao,
    private val tableCellDao: TableCellDao,
    private val syncQueueDao: SyncQueueDao,
    private val userPrefs: UserPrefs,
    private val syncScheduler: SyncScheduler,
    private val alarmScheduler: AlarmScheduler
) : NoteRepository {

    override fun observeNotes(userId: String): Flow<List<Note>> = noteDao.observeNotes(userId).map { entities ->
        entities.map { entity ->
            entity.toNote(
                checklist = checklistDao.getByNoteId(entity.noteId).toChecklistItems(),
                table = tableCellDao.getByNoteId(entity.noteId).toCells()
            )
        }
    }

    override suspend fun getNote(noteId: String): Note? = noteDao.getById(noteId)?.let { entity ->
        entity.toNote(
            checklist = checklistDao.getByNoteId(noteId).toChecklistItems(),
            table = tableCellDao.getByNoteId(noteId).toCells()
        )
    }

    override suspend fun upsert(note: Note) {
        noteDao.upsert(note.toEntity())
        checklistDao.deleteByNoteId(note.noteId)
        tableCellDao.deleteByNoteId(note.noteId)
        checklistDao.upsert(toChecklistEntities(note.noteId, note.checklistItems))
        tableCellDao.upsert(toTableEntities(note.noteId, note.tableData))

        syncQueueDao.enqueue(SyncQueueEntity(noteId = note.noteId, operation = "UPSERT", enqueuedAt = System.currentTimeMillis()))
        syncScheduler.enqueueSync()
        note.alarmTime?.let { alarmScheduler.schedule(note.noteId, note.title, note.description, it) }
    }

    override suspend fun delete(noteId: String) {
        checklistDao.deleteByNoteId(noteId)
        tableCellDao.deleteByNoteId(noteId)
        noteDao.delete(noteId)
        syncQueueDao.enqueue(SyncQueueEntity(noteId = noteId, operation = "DELETE", enqueuedAt = System.currentTimeMillis()))
        syncScheduler.enqueueSync()
    }

    override suspend fun syncPendingNotes() {
        // Placeholder for Google Drive push/pull.
        val queue = syncQueueDao.getAll()
        queue.forEach { item ->
            noteDao.updateSyncState(item.noteId, synced = true, updatedAt = System.currentTimeMillis())
            syncQueueDao.delete(item.id)
        }
    }

    override suspend fun saveSession(session: UserSession) = userPrefs.save(session)

    override fun observeSession(): Flow<UserSession> = userPrefs.observeSession()

    override suspend fun signOut() = userPrefs.clear()

    override suspend fun rescheduleAlarms() {
        // For brevity we rely on normal note save flow and optional manual refresh in app startup.
    }
}
