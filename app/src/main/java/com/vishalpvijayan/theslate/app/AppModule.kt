package com.vishalpvijayan.theslate.app

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.vishalpvijayan.theslate.data.local.AlarmDao
import com.vishalpvijayan.theslate.data.local.ChecklistDao
import com.vishalpvijayan.theslate.data.local.NoteDao
import com.vishalpvijayan.theslate.data.local.SyncQueueDao
import com.vishalpvijayan.theslate.data.local.TableCellDao
import com.vishalpvijayan.theslate.data.local.TheSlateDatabase
import com.vishalpvijayan.theslate.data.repository.NoteRepositoryImpl
import com.vishalpvijayan.theslate.domain.repository.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppProvidesModule {
    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): TheSlateDatabase =
        Room.databaseBuilder(context, TheSlateDatabase::class.java, "theslate.db").build()

    @Provides fun provideNoteDao(db: TheSlateDatabase): NoteDao = db.noteDao()
    @Provides fun provideChecklistDao(db: TheSlateDatabase): ChecklistDao = db.checklistDao()
    @Provides fun provideTableDao(db: TheSlateDatabase): TableCellDao = db.tableDao()
    @Provides fun provideQueueDao(db: TheSlateDatabase): SyncQueueDao = db.syncQueueDao()
    @Provides fun provideAlarmDao(db: TheSlateDatabase): AlarmDao = db.alarmDao()
    @Provides fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModule {
    @Binds
    @Singleton
    abstract fun bindNoteRepo(impl: NoteRepositoryImpl): NoteRepository
}
