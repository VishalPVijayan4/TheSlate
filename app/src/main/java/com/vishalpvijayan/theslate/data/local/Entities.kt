package com.vishalpvijayan.theslate.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val noteId: String,
    val title: String,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long,
    val imageAttachmentsJson: String,
    val audioAttachmentsJson: String,
    val tagsJson: String,
    val drawingImagePath: String?,
    val alarmTime: Long?,
    val isSynced: Boolean,
    val userId: String
)

@Entity(tableName = "checklist_items", primaryKeys = ["noteId", "idx"])
data class ChecklistItemEntity(
    val noteId: String,
    val idx: Int,
    val text: String,
    val isChecked: Boolean
)

@Entity(tableName = "table_cells", primaryKeys = ["noteId", "rowIdx", "columnIdx"])
data class TableCellEntity(
    val noteId: String,
    val rowIdx: Int,
    val columnIdx: Int,
    val value: String
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteId: String,
    val operation: String,
    val enqueuedAt: Long
)
