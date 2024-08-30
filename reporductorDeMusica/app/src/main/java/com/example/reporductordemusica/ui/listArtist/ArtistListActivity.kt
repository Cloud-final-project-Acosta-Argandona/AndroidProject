package com.example.reporductordemusica.ui.listArtist

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.reporductordemusica.R
import com.example.reporductordemusica.ViewModel.ArtistListViewModel
import com.example.reporductordemusica.ui.artist.ArtistAdapter

class ArtistListActivity : AppCompatActivity() {

    private val artistListViewModel: ArtistListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_list)

        val listView = findViewById<ListView>(R.id.listArtist)
        val adapter = ArtistAdapter(this, mutableListOf())
        listView.adapter = adapter

        artistListViewModel.artists.observe(this) { artists ->
            adapter.clear()
            adapter.addAll(artists)
            adapter.notifyDataSetChanged()
        }

        artistListViewModel.fetchArtists()
    }
}
