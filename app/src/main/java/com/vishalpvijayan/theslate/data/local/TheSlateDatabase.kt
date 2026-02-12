package com.vishalpvijayan.theslate.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NoteEntity::class, ChecklistItemEntity::class, TableCellEntity::class, SyncQueueEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TheSlateDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun tableDao(): TableCellDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun alarmDao(): AlarmDao
}
