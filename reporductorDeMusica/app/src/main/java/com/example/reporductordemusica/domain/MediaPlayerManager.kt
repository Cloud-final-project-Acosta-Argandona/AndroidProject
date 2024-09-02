package com.example.reporductordemusica.domain

import android.content.Context
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer

class MediaPlayerManager(context: Context) {
    private val player = SimpleExoPlayer.Builder(context).build()
    private var playbackPosition = 0L

    fun playSong(songUrl: String, position: Long = 0L) {
        val mediaItem = MediaItem.fromUri(songUrl)
        player.apply {
            setMediaItem(mediaItem)
            seekTo(position)
            prepare()
            playWhenReady = true
        }
    }

    fun pause() {
        playbackPosition = player.currentPosition
        player.pause()
    }

    fun play() {
        player.play()
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    fun getCurrentPosition(): Long {
        return player.currentPosition
    }

    fun release() {
        player.release()
    }
}
