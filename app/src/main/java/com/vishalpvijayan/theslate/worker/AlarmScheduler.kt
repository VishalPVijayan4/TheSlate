package com.vishalpvijayan.theslate.worker

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(private val workManager: WorkManager) {
    fun schedule(noteId: String, title: String, description: String, triggerAt: Long) {
        val delay = (triggerAt - System.currentTimeMillis()).coerceAtLeast(0)
        val request = OneTimeWorkRequestBuilder<AlarmWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                Data.Builder().putString("title", title).putString("description", description).build()
            )
            .build()
        workManager.enqueueUniqueWork("alarm_$noteId", ExistingWorkPolicy.REPLACE, request)
    }
}
