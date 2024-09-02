package com.example.reporductordemusica.domain

import android.content.Context
import android.os.Bundle
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.analytics.AnalyticsListener.EventTime
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MediaPlayerManager(context: Context) {

    private val player = SimpleExoPlayer.Builder(context).build()
    private var playbackPosition = 0L
    private val analytics = FirebaseAnalytics.getInstance(context)
    private val crashlytics = FirebaseCrashlytics.getInstance()

    init {
        player.addAnalyticsListener(object : AnalyticsListener {
            override fun onPlayerStateChanged(eventTime: EventTime, playWhenReady: Boolean, playbackState: Int) {
                val state = when (playbackState) {
                    SimpleExoPlayer.STATE_IDLE -> "IDLE"
                    SimpleExoPlayer.STATE_BUFFERING -> "BUFFERING"
                    SimpleExoPlayer.STATE_READY -> "READY"
                    SimpleExoPlayer.STATE_ENDED -> "ENDED"
                    else -> "UNKNOWN"
                }
                analytics.logEvent("player_state_changed", Bundle().apply {
                    putString("state", state)
                    putBoolean("play_when_ready", playWhenReady)
                })
            }

            override fun onPlayerError(eventTime: EventTime, error: PlaybackException) {
                crashlytics.setCustomKey("Error_Code", error.errorCode)
                crashlytics.setCustomKey("Error_Cause", error.cause?.toString() ?: "Unknown")
                crashlytics.log("Error occurred during playback.")
                crashlytics.recordException(error)

                analytics.logEvent("player_error", Bundle().apply {
                    putString("error", error.message)
                })
            }
        })
    }

    fun playSong(songUrl: String, position: Long = 0L) {
        try {
            val mediaItem = MediaItem.fromUri(songUrl)
            player.apply {
                setMediaItem(mediaItem)
                seekTo(position)
                prepare()
                playWhenReady = true
            }
            analytics.logEvent("play_song", Bundle().apply {
                putString("song_url", songUrl)
                putLong("position", position)
            })
        } catch (e: Exception) {
            crashlytics.setCustomKey("Song_URL", songUrl)
            crashlytics.setCustomKey("Playback_Position", position)
            crashlytics.log("Exception occurred while playing the song.")
            crashlytics.recordException(e)
        }
    }

    fun pause() {
        playbackPosition = player.currentPosition
        player.pause()
        analytics.logEvent("pause_song", null)
    }

    fun play() {
        player.play()
        analytics.logEvent("resume_song", null)
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    fun getCurrentPosition(): Long {
        return player.currentPosition
    }

    fun release() {
        player.release()
        analytics.logEvent("release_player", null)
    }
}
