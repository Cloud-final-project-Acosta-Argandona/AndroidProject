// ui/FavoriteSongsAdapter.kt
package com.example.reporductordemusica.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.reporductordemusica.R
import com.example.reporductordemusica.domain.Song

class FavoriteSongsAdapter(context: Context, songs: List<Song>) :
    ArrayAdapter<Song>(context, 0, songs) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val song = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_fav_song, parent, false)

        val songName = view.findViewById<TextView>(R.id.textViewNameFavSong)
        val artistName = view.findViewById<TextView>(R.id.artistnamesongfav)

        songName.text = song?.name
        artistName.text = song?.artist

        return view
    }
}
