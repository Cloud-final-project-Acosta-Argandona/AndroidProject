package com.example.reporductordemusica

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.reporductordemusica.ViewModel.FavoriteSongsViewModel
import com.example.reporductordemusica.ui.FavoriteSongsAdapter
import com.example.reporductordemusica.ui.listArtist.ArtistListActivity

import com.google.firebase.analytics.FirebaseAnalytics

class FavoriteSongsActivity : AppCompatActivity() {

    private lateinit var viewModel: FavoriteSongsViewModel
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorite_songs)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        viewModel = ViewModelProvider(this).get(FavoriteSongsViewModel::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.floatingActionButton6).setOnClickListener {
            val intent = Intent(this, ArtistListActivity::class.java)
            startActivity(intent)

            firebaseAnalytics.logEvent("navigate_to_artist_list", Bundle())
        }

        viewModel.favoriteSongs.observe(this) { songArtistMap ->
            val listView = findViewById<ListView>(R.id.ListViewFavSongs)
            val adapter = FavoriteSongsAdapter(
                this,
                songArtistMap,
                viewModel.currentlyPlayingSong.value,
                onPlayClick = { song ->
                    val playBundle = Bundle().apply {
                        putString("song_title", song.name)
                        putString("artist_name", song.artist)
                    }
                    firebaseAnalytics.logEvent("play_favorite_song", playBundle)

                    viewModel.togglePlayPause(song)
                },
                onRemoveClick = { song ->
                    val removeBundle = Bundle().apply {
                        putString("song_title", song.name)
                        putString("artist_name", song.artist)
                    }
                    firebaseAnalytics.logEvent("remove_favorite_song", removeBundle)

                    viewModel.removeFavoriteSong(song)
                }
            )
            listView.adapter = adapter

            viewModel.currentlyPlayingSong.observe(this) { currentlyPlayingSong ->
                (listView.adapter as? FavoriteSongsAdapter)?.updateCurrentlyPlayingSong(currentlyPlayingSong)
            }
        }

        firebaseAnalytics.logEvent("view_favorite_songs_screen", Bundle())
    }
}

