package com.example.reporductordemusica.ui.songs

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.reporductordemusica.ViewModel.SongListViewModel
import com.example.reporductordemusica.R
import com.example.reporductordemusica.domain.Song
import com.example.reporductordemusica.domain.UserRepository
import com.example.reporductordemusica.ui.listArtist.ArtistListActivity

class ViewSongsActivity : AppCompatActivity() {
    private val songListViewModel: SongListViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null
    private var lastPosition: Int = 0
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_view_songs)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val artistId = intent.getStringExtra("ARTIST_ID")
        val listView = findViewById<ListView>(R.id.listMusicView)
        val adapter = SongAdapter(this, mutableListOf(), ::playPauseSong, userRepository)
        listView.adapter = adapter

        artistId?.let {
            songListViewModel.fetchSongs(it)
        }

        songListViewModel.songs.observe(this) { songs ->
            adapter.clear()
            adapter.addAll(songs)
            adapter.notifyDataSetChanged()
        }

        songListViewModel.artist.observe(this) { artist ->
            val artistNameTextView = findViewById<TextView>(R.id.textViewArtistName)
            val genreTextView = findViewById<TextView>(R.id.textView4)

            artistNameTextView.text = artist?.name
            genreTextView.text = "Género: ${artist?.genre}"
        }

        val floatingActionButton = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.floatingActionButton3)
        floatingActionButton.setOnClickListener {
            val intent = Intent(this, ArtistListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun playPauseSong(song: Song) {
        if (mediaPlayer?.isPlaying == true) {
            lastPosition = mediaPlayer?.currentPosition ?: 0
            mediaPlayer?.pause()
        } else {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(song.storageUrl)
                prepare()
                seekTo(lastPosition)
                start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        lastPosition = 0
    }
}
