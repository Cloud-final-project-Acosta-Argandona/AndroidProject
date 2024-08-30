package com.example.reporductordemusica.ui.songs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.reporductordemusica.R
import com.example.reporductordemusica.domain.Song

class SongAdapter(
    context: Context,
    private val songs: MutableList<Song>,
    private val playPauseCallback: (Song) -> Unit,
) : ArrayAdapter<Song>(context, 0, songs) {

    private var currentlyPlayingSong: Song? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val song = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_music, parent, false)
        val nameTextView = view.findViewById<TextView>(R.id.textViewNombreMusic)
        val playButton = view.findViewById<ImageView>(R.id.floatingActionButton)
        val addButton = view.findViewById<ImageView>(R.id.floatingActionButton2)

        nameTextView.text = song?.name

        if (song == currentlyPlayingSong) {
            playButton.setImageResource(android.R.drawable.ic_media_pause)
        } else {
            playButton.setImageResource(android.R.drawable.ic_media_play)
        }

        playButton.setOnClickListener {
            song?.let {
                playPauseCallback(it)
                currentlyPlayingSong = if (currentlyPlayingSong == it) null else it
                notifyDataSetChanged()
            }
        }

        addButton.setOnClickListener{

        }

        return view
    }
}