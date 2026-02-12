package com.vishalpvijayan.theslate.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ChecklistItem(
    val text: String,
    val isChecked: Boolean
)

@Serializable
data class TableCell(
    val row: Int,
    val column: Int,
    val value: String
)

@Serializable
data class Note(
    val noteId: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val imageAttachments: List<String> = emptyList(),
    val audioAttachments: List<String> = emptyList(),
    val drawingImagePath: String? = null,
    val checklistItems: List<ChecklistItem> = emptyList(),
    val tableData: List<TableCell> = emptyList(),
    val tags: List<String> = emptyList(),
    val alarmTime: Long? = null,
    val isSynced: Boolean = false,
    val userId: String = "local_user"
)

@Serializable
data class UserSession(
    val userId: String = "",
    val userName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val isLoggedIn: Boolean = false
)
