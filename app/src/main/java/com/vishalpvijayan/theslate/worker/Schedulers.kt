package com.vishalpvijayan.theslate.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.vishalpvijayan.theslate.domain.repository.NoteRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@HiltWorker
class SyncNotesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: NoteRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        repository.syncPendingNotes()
        return Result.success()
    }
}

@HiltWorker
class AlarmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        NotificationHelper.showAlarmNotification(
            applicationContext,
            inputData.getString("title").orEmpty(),
            inputData.getString("description").orEmpty()
        )
        return Result.success()
    }
}

@Singleton
class SyncScheduler @Inject constructor(private val workManager: WorkManager) {
    fun enqueueSync() {
        val request = OneTimeWorkRequestBuilder<SyncNotesWorker>()
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()
        workManager.enqueueUniqueWork("notes_sync", ExistingWorkPolicy.KEEP, request)
    }
}
