package com.example.reporductordemusica.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.reporductordemusica.R
import com.example.reporductordemusica.domain.Song
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FavoriteSongsAdapter(context: Context, private val songArtistMap: Map<Song, String>) :
    ArrayAdapter<Song>(context, 0, songArtistMap.keys.toList()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val song = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_fav_song, parent, false)

        val songName = view.findViewById<TextView>(R.id.textViewNameFavSong)
        val artistName = view.findViewById<TextView>(R.id.artistnamesongfav)
        val floatingAddFav = view.findViewById<FloatingActionButton>(R.id.floatingActionButton7)
        val floatingReproduce = view.findViewById<FloatingActionButton>(R.id.floatingActionButton5)

        song?.let {
            songName.text = it.name
            artistName.text = songArtistMap[it]
        }

        return view
    }
}
