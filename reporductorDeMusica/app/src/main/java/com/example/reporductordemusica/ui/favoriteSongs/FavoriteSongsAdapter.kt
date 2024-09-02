package com.example.reporductordemusica.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.reporductordemusica.R
import com.example.reporductordemusica.domain.Song
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class FavoriteSongsAdapter(
    context: Context,
    private val songArtistMap: Map<Song, String>,
    private var currentlyPlayingSong: Song?,
    private val onPlayClick: (Song) -> Unit,
    private val onRemoveClick: (Song) -> Unit
) : ArrayAdapter<Song>(context, 0, songArtistMap.keys.toList()) {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val song = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_fav_song, parent, false)

        val songName = view.findViewById<TextView>(R.id.textViewNameFavSong)
        val artistName = view.findViewById<TextView>(R.id.artistnamesongfav)
        val floatingReproduce = view.findViewById<FloatingActionButton>(R.id.floatingActionButton5)
        val floatingAddFav = view.findViewById<FloatingActionButton>(R.id.floatingActionButton7)

        song?.let { songItem ->
            songName.text = songItem.name
            artistName.text = songArtistMap[songItem]

            val isPlaying = currentlyPlayingSong == songItem
            floatingReproduce.setImageResource(
                if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
            )

            floatingReproduce.setOnClickListener {
                try {
                    onPlayClick(songItem)
                    val bundle = Bundle().apply {
                        putString("song_name", songItem.name)
                        putString("artist_name", songArtistMap[songItem])
                    }
                    firebaseAnalytics.logEvent("play_song", bundle)
                } catch (e: Exception) {
                    crashlytics.recordException(e)
                }
            }

            floatingAddFav.setOnClickListener {
                try {
                    onRemoveClick(songItem)
                    val bundle = Bundle().apply {
                        putString("song_name", songItem.name)
                        putString("artist_name", songArtistMap[songItem])
                    }
                    firebaseAnalytics.logEvent("remove_favorite_song", bundle)
                } catch (e: Exception) {
                    crashlytics.recordException(e)
                }
            }
        }

        return view
    }

    fun updateCurrentlyPlayingSong(newSong: Song?) {
        currentlyPlayingSong = newSong
        notifyDataSetChanged()
    }
}
