package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*

const val TRACK_KEY = "track"

class PlayerActivity : AppCompatActivity() {

    lateinit var playerBackButton: ImageView
    lateinit var currentTrack: Track
    lateinit var albumCover: ImageView
    lateinit var trackName: TextView
    lateinit var artistName: TextView
    lateinit var trackDuration: TextView
    lateinit var trackAlbum: TextView
    lateinit var trackYear: TextView
    lateinit var trackGenre: TextView
    lateinit var trackCountry: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerBackButton = findViewById(R.id.playerBack)
        albumCover = findViewById(R.id.albumCover)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackDuration = findViewById(R.id.durationTime)
        trackAlbum = findViewById(R.id.albumName)
        trackYear = findViewById(R.id.releaseDate)
        trackGenre = findViewById(R.id.genreName)
        trackCountry = findViewById(R.id.countryName)

        currentTrack = intent.getSerializableExtra(TRACK_KEY) as Track
        viewFinder()
        setOnClickListeners()
    }

    private fun setOnClickListeners(){
        playerBackButton.setOnClickListener {
            finish()
        }
    }

    private fun viewFinder() {
        Glide.with(applicationContext)
                .load(currentTrack.getCoverArtwork())
                .transform(RoundedCorners(8))
                .placeholder(R.drawable.album_cover_placeholder)
                .into(albumCover)

            trackName.text = currentTrack.trackName
            artistName.text = currentTrack.artistName
            trackDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTimeMillis.toLong())
            trackAlbum.text = currentTrack.collectionName
            trackYear.text = currentTrack.releaseDate.substring(0, 4)
            trackGenre.text = currentTrack.primaryGenreName
            trackCountry.text = currentTrack.country
    }
}