package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AudioPlayerActivity : AppCompatActivity() {

    private enum class PlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED,
    }

    private lateinit var playButton: ImageButton
    private var playButtonIsPressed: Boolean = false
    private var playerState: PlayerState = PlayerState.DEFAULT
    private var mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setData()
        setOnClickListeners()

    }

    private fun setData() {
        val track = intent.getSerializableExtra("Track") as Track
        val pic = PlaylistUtil.getHigherResolutionPic(track.artworkUrl100)
        val imageView = findViewById<ImageView>(R.id.player_song_image)
        PlaylistUtil.loadPicInto(this, pic, imageView)
        findViewById<TextView>(R.id.player_song_title).text = track.trackName
        findViewById<TextView>(R.id.player_artist).text = track.artistName
        findViewById<TextView>(R.id.duration_time).text = PlaylistUtil.getFormattedTime(track.trackTimeMillis)
        findViewById<TextView>(R.id.collection_name).text = track.collectionName
        findViewById<TextView>(R.id.song_year).text = track.releaseDate.substring(0, 4)
        findViewById<TextView>(R.id.genre_name).text = track.primaryGenreName
        findViewById<TextView>(R.id.country_name).text = track.country
        preparePlayer(track.previewUrl)
    }

    private fun setOnClickListeners() {
        findViewById<ImageView>(R.id.back_player_btn).setOnClickListener {
            finish()
        }
        playButton = findViewById(R.id.play_button)
        playButton.isEnabled = false
        playButton.setOnClickListener {
            playButtonIsPressed = !playButtonIsPressed
        }
    }

    private fun preparePlayer(songUrl: String) {
        mediaPlayer.setDataSource(songUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = PlayerState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.PREPARED
        }
    }
}