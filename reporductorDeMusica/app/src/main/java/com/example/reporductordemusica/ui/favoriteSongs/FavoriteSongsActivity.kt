package com.example.reporductordemusica

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.reporductordemusica.domain.ArtistRepository
import com.example.reporductordemusica.domain.Song
import com.example.reporductordemusica.domain.SongRepository
import com.example.reporductordemusica.ui.FavoriteSongsAdapter
import com.example.reporductordemusica.ui.listArtist.ArtistListActivity
import com.example.reporductordemusica.domain.UserRepository
import kotlinx.coroutines.launch
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer

class FavoriteSongsActivity : AppCompatActivity() {
    private val userRepository = UserRepository()
    private val songRepository = SongRepository()
    private val artistRepository = ArtistRepository()

    var player: SimpleExoPlayer? = null
    private var currentlyPlayingSong: Song? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorite_songs)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.floatingActionButton6).setOnClickListener {
            val intent = Intent(this, ArtistListActivity::class.java)
            startActivity(intent)
        }

        val currentUserEmail = userRepository.auth.currentUser?.email.orEmpty()
        if (currentUserEmail.isNotEmpty()) {
            userRepository.getUserDetails(
                userEmail = currentUserEmail,
                onSuccess = { user ->
                    val songIds = user.idSongs
                    if (songIds.isNotEmpty()) {
                        lifecycleScope.launch {
                            try {
                                val favoriteSongs = songRepository.getFavoriteSongsByIds(songIds)
                                val songArtistMap = mutableMapOf<Song, String>()

                                for (song in favoriteSongs) {
                                    val artist = artistRepository.getArtistById(song.artist)
                                    songArtistMap[song] = artist?.name.orEmpty()
                                }

                                val listView = findViewById<ListView>(R.id.ListViewFavSongs)
                                val adapter = FavoriteSongsAdapter(
                                    this@FavoriteSongsActivity,
                                    songArtistMap,
                                    currentlyPlayingSong,
                                    onPlayClick = { song ->
                                        togglePlayPause(song)
                                    }
                                )
                                listView.adapter = adapter

                            } catch (e: Exception) {
                                Log.e("FavoriteSongsActivity", "Failed to get favorite songs", e)
                            }
                        }
                    }
                },
                onFailure = { exception ->
                    Log.e("FavoriteSongsActivity", "Failed to get user details", exception)
                }
            )
        } else {
            Log.w("FavoriteSongsActivity", "No user is currently logged in")
        }
    }

    private fun togglePlayPause(song: Song) {
        if (currentlyPlayingSong == song && player?.isPlaying == true) {
            player?.pause()
        } else {
            if (currentlyPlayingSong != song) {
                playSong(song)
            } else {
                player?.play()
            }
        }
        val listView = findViewById<ListView>(R.id.ListViewFavSongs)
        (listView.adapter as FavoriteSongsAdapter).notifyDataSetChanged()
    }

    private fun playSong(song: Song) {
        if (player == null) {
            player = SimpleExoPlayer.Builder(this).build()
        }

        if (currentlyPlayingSong == song) {
            if (player?.isPlaying == true) {
                player?.pause()
            } else {
                player?.play()
            }
        } else {
            val mediaItem = MediaItem.fromUri(song.storageUrl)
            player?.apply {
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
            currentlyPlayingSong = song
        }

        val listView = findViewById<ListView>(R.id.ListViewFavSongs)
        (listView.adapter as FavoriteSongsAdapter).updateCurrentlyPlayingSong(currentlyPlayingSong)
    }


    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
