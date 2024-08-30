package com.example.reporductordemusica.ui.artist

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.reporductordemusica.R
import com.example.reporductordemusica.domain.Artist
import com.example.reporductordemusica.ui.songs.ViewSongsActivity

class ArtistAdapter(context: Context, private val artists: MutableList<Artist>) :
    ArrayAdapter<Artist>(context, 0, artists) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val artist = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_artist, parent, false)
        val nameTextView = view.findViewById<TextView>(R.id.textViewNombreMusic)
        val genreTextView = view.findViewById<TextView>(R.id.textViewGeneroArtist)
        val imageView = view.findViewById<ImageView>(R.id.imageViewArtist)
        imageView.setImageResource(R.drawable.ic_launcher_background)
        nameTextView.text = artist?.name
        genreTextView.text = artist?.genre

        view.setOnClickListener {
            val intent = Intent(context, ViewSongsActivity::class.java)
            intent.putExtra("ARTIST_ID", artist?.id)
            context.startActivity(intent)
        }
        return view
    }
}
