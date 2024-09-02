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

class FavoriteSongsActivity : AppCompatActivity() {

    private lateinit var viewModel: FavoriteSongsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorite_songs)

        viewModel = ViewModelProvider(this).get(FavoriteSongsViewModel::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.floatingActionButton6).setOnClickListener {
            val intent = Intent(this, ArtistListActivity::class.java)
            startActivity(intent)
        }

        viewModel.favoriteSongs.observe(this) { songArtistMap ->
            val listView = findViewById<ListView>(R.id.ListViewFavSongs)
            val adapter = FavoriteSongsAdapter(
                this,
                songArtistMap,
                viewModel.currentlyPlayingSong.value,
                onPlayClick = { song ->
                    viewModel.togglePlayPause(song)
                }
            )
            listView.adapter = adapter

            viewModel.currentlyPlayingSong.observe(this) { currentlyPlayingSong ->
                (listView.adapter as? FavoriteSongsAdapter)?.updateCurrentlyPlayingSong(currentlyPlayingSong)
            }
        }

        viewModel.loadFavoriteSongs()
    }
}

