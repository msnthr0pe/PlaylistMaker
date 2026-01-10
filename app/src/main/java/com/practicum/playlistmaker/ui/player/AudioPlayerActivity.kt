package com.practicum.playlistmaker.ui.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.PlaylistUtil
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.player.viewmodel.PlayerHelper

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var currentTrackTime: TextView
    private var mainThreadHandler: Handler? = null
    private var playerHelper = PlayerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mainThreadHandler = Handler(Looper.getMainLooper())
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
        currentTrackTime = findViewById(R.id.current_track_time)
        playerHelper.preparePlayer(
            mainThreadHandler,
            track.previewUrl,
            {
            playButton.isEnabled = true
        },
            {
                setPlayButtonImage()
            })
    }

    private fun setOnClickListeners() {
        findViewById<ImageView>(R.id.back_player_btn).setOnClickListener {
            finish()
        }
        playButton = findViewById(R.id.play_button)
        //playButton.isEnabled = false
        playButton.setOnClickListener {
            playerHelper.playbackControl { playbackPosition ->
                currentTrackTime.text = playbackPosition
            }
            setPlayButtonImage()
        }
    }

    private fun setPlayButtonImage() {
        playButton.setImageResource(
            if (playerHelper.isPlaying){
                R.drawable.ic_pause
            } else R.drawable.ic_play
        )
    }

    override fun onPause() {
        super.onPause()
        playerHelper.onPause()
        setPlayButtonImage()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerHelper.onDestroy()
        setPlayButtonImage()
    }
}