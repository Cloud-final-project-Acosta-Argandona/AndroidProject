package com.example.reporductordemusica.ui.listArtist

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.reporductordemusica.FavoriteSongsActivity
import com.example.reporductordemusica.R
import com.example.reporductordemusica.ViewModel.ArtistListViewModel
import com.example.reporductordemusica.ui.artist.ArtistAdapter
import com.google.firebase.analytics.FirebaseAnalytics

class ArtistListActivity : AppCompatActivity() {

    private val artistListViewModel: ArtistListViewModel by viewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_list)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val listView = findViewById<ListView>(R.id.listArtist)

        val adapter = ArtistAdapter(this, mutableListOf(), firebaseAnalytics)
        listView.adapter = adapter

        artistListViewModel.artists.observe(this) { artists ->
            adapter.clear()
            adapter.addAll(artists)
            adapter.notifyDataSetChanged()
        }

        artistListViewModel.fetchArtists()

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.floatingActionButton4).setOnClickListener {
            val intent = Intent(this, FavoriteSongsActivity::class.java)
            startActivity(intent)
        }
    }
}
