package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*

const val TRACK_KEY = "track"


class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val PLAYER_STATE_DEFAULT = 0
        private const val PLAYER_STATE_PREPARED = 1
        private const val PLAYER_STATE_PLAYING = 2
        private const val PLAYER_STATE_PAUSED = 3

        private const val DELAY = 500L
    }

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
    lateinit var playButton: ImageView
    lateinit var timer: TextView

    private var playerState = PLAYER_STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    val durationRunnable = setDuration()
    private var handler : Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        handler = Handler(Looper.getMainLooper())

        playerBackButton = findViewById(R.id.playerBack)
        albumCover = findViewById(R.id.albumCover)
        trackName = findViewById(R.id.trackName)
        trackName.setSelected(true)
        artistName = findViewById(R.id.artistName)
        artistName.setSelected(true)
        trackDuration = findViewById(R.id.durationTime)
        trackAlbum = findViewById(R.id.albumName)
        trackYear = findViewById(R.id.releaseDate)
        trackGenre = findViewById(R.id.genreName)
        trackCountry = findViewById(R.id.countryName)
        playButton = findViewById(R.id.playButton)
        timer = findViewById(R.id.timer)

        currentTrack = intent.getSerializableExtra(TRACK_KEY) as Track
        viewFinder()
        setOnClickListeners()
        preparePlayer()

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        handler?.removeCallbacks(durationRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler?.removeCallbacks(durationRunnable)
    }

    private fun setOnClickListeners(){
        playerBackButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            playbackControl()
        }
    }

    private fun viewFinder() {
        Glide.with(applicationContext)
                .load(currentTrack.getCoverArtwork())
                .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.artwork_rounded_corners)))
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

    private fun preparePlayer() {
        mediaPlayer.setDataSource(currentTrack.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.setImageResource(R.drawable.play)
            playerState = PLAYER_STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.play)
            playerState = PLAYER_STATE_PREPARED
            handler?.removeCallbacks(durationRunnable)
            timer.text = "00:00"
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.pause)
        playerState = PLAYER_STATE_PLAYING
        handler?.post(durationRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.play)
        playerState = PLAYER_STATE_PAUSED
    }

    private fun playbackControl() {
        when(playerState) {
            PLAYER_STATE_PLAYING -> {
                pausePlayer()

            }
            PLAYER_STATE_PREPARED, PLAYER_STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun setDuration() : Runnable {
        return object : Runnable {
            override fun run() {
                timer.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                handler?.postDelayed(this, DELAY)
            }
        }

    }
}