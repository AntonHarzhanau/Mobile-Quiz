package com.example.musicalquizz.db.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.musicalquizz.db.AppDatabase
import com.example.musicalquizz.db.repository.PlaylistRepository
import java.io.File
import java.net.URL

class PreviewDownloadWorker(
    context: Context, params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val uid = inputData.getLong("uid", -1)
        val url = inputData.getString("previewUrl") ?: return Result.failure()

        try {
            // Load mp3 in internal storage
            val file = File(applicationContext.filesDir, "preview_$uid.mp3")
            URL(url).openStream().use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }

            // Refresh DB
            val repo = PlaylistRepository(
                AppDatabase.getInstance(applicationContext),
                applicationContext
            )
            repo.updateLocalPath(uid, file.absolutePath)

            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }
}
