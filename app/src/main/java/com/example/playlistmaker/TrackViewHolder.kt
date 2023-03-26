package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*

class TrackViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.item_track, parentView, false)
) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val artworkUrl: ImageView = itemView.findViewById(R.id.artwork)

    fun bind(element: Track) {
        trackName.text = element.trackName
        artistName.text = element.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(element.trackTimeMillis.toLong())
        Glide.with(itemView)
            .load(element.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(2))
            .placeholder(R.drawable.pic_lost)
            .into(artworkUrl)
    }
}