package com.practicum.playlistmaker.ui.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.PlaylistUtil
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.player.viewmodel.PlayerHelper

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var playButton: ImageButton
    private lateinit var currentTrackTime: TextView
    private var mainThreadHandler: Handler? = null
    private var playerHelper = PlayerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
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
        val imageView = binding.playerSongImage
        PlaylistUtil.loadPicInto(this, pic, imageView)

        binding.apply {
            playerSongTitle.text = track.trackName
            playerArtist.text = track.artistName
            durationTime.text = PlaylistUtil.getFormattedTime(track.trackTimeMillis)
            collectionName.text = track.collectionName
            songYear.text = track.releaseDate.substring(0, 4)
            genreName.text = track.primaryGenreName
            countryName.text = track.country
        }

        currentTrackTime = binding.currentTrackTime

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
        binding.apply {
            backPlayerBtn.setOnClickListener {
                finish()
            }
        }
        playButton = binding.playButton
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