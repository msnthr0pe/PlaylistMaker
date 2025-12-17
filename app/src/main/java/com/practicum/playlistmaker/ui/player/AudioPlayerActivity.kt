package com.practicum.playlistmaker.ui.player

import android.media.MediaPlayer
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
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private enum class PlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED,
    }

    private lateinit var playButton: ImageButton
    private var isPlaying: Boolean = false
    private var playerState: PlayerState = PlayerState.DEFAULT
    private var mediaPlayer = MediaPlayer()
    private lateinit var currentTrackTime: TextView
    private var mainThreadHandler: Handler? = null

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
        preparePlayer(track.previewUrl)
    }

    private fun setOnClickListeners() {
        findViewById<ImageView>(R.id.back_player_btn).setOnClickListener {
            finish()
        }
        playButton = findViewById(R.id.play_button)
        playButton.isEnabled = false
        playButton.setOnClickListener {
            isPlaying = !isPlaying
            setPlayButtonImage()
            playbackControl()
        }
    }

    private fun setPlayButtonImage() {
        playButton.setImageResource(
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )
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
            mediaPlayer.seekTo(0)
            isPlaying = false
            setPlayButtonImage()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
        startHandler()
    }

    private fun startHandler() {
        mainThreadHandler?.postDelayed(
            object : Runnable {
                override fun run() {
                    val playbackPosition = SimpleDateFormat("m:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                    currentTrackTime.text = playbackPosition
                    mainThreadHandler?.postDelayed(
                        this,
                        PLAYBACK_PROGRESS_REFRESH_DELAY
                    )
                }
            },
            PLAYBACK_PROGRESS_REFRESH_DELAY
        )
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

    private fun playbackControl() {
        when(playerState) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
            }
            PlayerState.DEFAULT -> Unit
        }
    }

    override fun onPause() {
        super.onPause()
        isPlaying = false
        setPlayButtonImage()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        isPlaying = false
        setPlayButtonImage()
        mediaPlayer.release()
    }

    companion object {
        private const val PLAYBACK_PROGRESS_REFRESH_DELAY = 300L
    }
}