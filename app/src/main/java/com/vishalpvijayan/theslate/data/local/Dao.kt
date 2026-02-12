package com.vishalpvijayan.theslate.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY updatedAt DESC")
    fun observeNotes(userId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE noteId = :noteId LIMIT 1")
    suspend fun getById(noteId: String): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: NoteEntity)

    @Query("DELETE FROM notes WHERE noteId = :noteId")
    suspend fun delete(noteId: String)

    @Query("UPDATE notes SET isSynced = :synced, updatedAt = :updatedAt WHERE noteId = :noteId")
    suspend fun updateSyncState(noteId: String, synced: Boolean, updatedAt: Long)
}

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist_items WHERE noteId = :noteId ORDER BY idx")
    suspend fun getByNoteId(noteId: String): List<ChecklistItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(items: List<ChecklistItemEntity>)

    @Query("DELETE FROM checklist_items WHERE noteId = :noteId")
    suspend fun deleteByNoteId(noteId: String)
}

@Dao
interface TableCellDao {
    @Query("SELECT * FROM table_cells WHERE noteId = :noteId ORDER BY rowIdx, columnIdx")
    suspend fun getByNoteId(noteId: String): List<TableCellEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cells: List<TableCellEntity>)

    @Query("DELETE FROM table_cells WHERE noteId = :noteId")
    suspend fun deleteByNoteId(noteId: String)
}

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue ORDER BY enqueuedAt ASC")
    suspend fun getAll(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(entity: SyncQueueEntity)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface AlarmDao {
    @Query("SELECT * FROM notes WHERE alarmTime IS NOT NULL AND alarmTime > :now")
    suspend fun getFutureAlarms(now: Long): List<NoteEntity>
}
