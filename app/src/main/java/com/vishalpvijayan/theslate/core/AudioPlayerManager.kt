package com.vishalpvijayan.theslate.core

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayerManager @Inject constructor(@ApplicationContext context: Context) {
    private val player = ExoPlayer.Builder(context).build()

    fun play(uri: String) {
        val mediaUri = if (uri.startsWith("/")) Uri.fromFile(java.io.File(uri)) else Uri.parse(uri)
        player.setMediaItem(MediaItem.fromUri(mediaUri))
        player.prepare()
        player.play()
    }

    fun stop() = player.stop()

    fun release() {
        player.release()
    }
}
