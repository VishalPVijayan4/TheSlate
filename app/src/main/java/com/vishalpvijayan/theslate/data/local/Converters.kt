package com.vishalpvijayan.theslate.data.local

import com.vishalpvijayan.theslate.domain.model.ChecklistItem
import com.vishalpvijayan.theslate.domain.model.Note
import com.vishalpvijayan.theslate.domain.model.TableCell
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object NoteMappers {
    private val json = Json { ignoreUnknownKeys = true }

    fun Note.toEntity(): NoteEntity = NoteEntity(
        noteId = noteId,
        title = title,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
        imageAttachmentsJson = json.encodeToString(imageAttachments),
        audioAttachmentsJson = json.encodeToString(audioAttachments),
        drawingImagePath = drawingImagePath,
        alarmTime = alarmTime,
        isSynced = isSynced,
        userId = userId
    )

    fun NoteEntity.toNote(checklist: List<ChecklistItem>, table: List<TableCell>): Note = Note(
        noteId = noteId,
        title = title,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
        imageAttachments = json.decodeFromString(imageAttachmentsJson),
        audioAttachments = json.decodeFromString(audioAttachmentsJson),
        drawingImagePath = drawingImagePath,
        checklistItems = checklist,
        tableData = table,
        alarmTime = alarmTime,
        isSynced = isSynced,
        userId = userId
    )

    fun toChecklistEntities(noteId: String, list: List<ChecklistItem>): List<ChecklistItemEntity> =
        list.mapIndexed { index, item ->
            ChecklistItemEntity(noteId = noteId, idx = index, text = item.text, isChecked = item.isChecked)
        }

    fun List<ChecklistItemEntity>.toChecklistItems(): List<ChecklistItem> =
        map { ChecklistItem(text = it.text, isChecked = it.isChecked) }

    fun toTableEntities(noteId: String, list: List<TableCell>): List<TableCellEntity> =
        list.map { cell ->
            TableCellEntity(noteId = noteId, rowIdx = cell.row, columnIdx = cell.column, value = cell.value)
        }

    fun List<TableCellEntity>.toCells(): List<TableCell> =
        map { TableCell(row = it.rowIdx, column = it.columnIdx, value = it.value) }
}
