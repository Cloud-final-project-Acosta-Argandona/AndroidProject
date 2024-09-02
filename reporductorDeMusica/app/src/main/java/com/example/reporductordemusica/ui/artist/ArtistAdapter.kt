package com.example.reporductordemusica.ui.artist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.reporductordemusica.R
import com.example.reporductordemusica.domain.Artist
import com.example.reporductordemusica.ui.songs.ViewSongsActivity
import com.google.firebase.analytics.FirebaseAnalytics

class ArtistAdapter(
    context: Context,
    private val artists: MutableList<Artist>,
    private val firebaseAnalytics: FirebaseAnalytics
) : ArrayAdapter<Artist>(context, 0, artists) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val artist = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_artist, parent, false)
        val nameTextView = view.findViewById<TextView>(R.id.textViewNombreMusic)
        val genreTextView = view.findViewById<TextView>(R.id.textViewGeneroArtist)
        nameTextView.text = artist?.name
        genreTextView.text = artist?.genre

        view.setOnClickListener {
            val bundle = Bundle().apply {
                putString("artist_name", artist?.name)
                putString("artist_genre", artist?.genre)
                putString("artist_id", artist?.id)
            }
            firebaseAnalytics.logEvent("select_artist", bundle)

            val intent = Intent(context, ViewSongsActivity::class.java)
            intent.putExtra("ARTIST_ID", artist?.id)
            context.startActivity(intent)
        }
        return view
    }
}

