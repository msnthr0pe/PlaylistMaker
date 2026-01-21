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
import com.practicum.playlistmaker.ui.player.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private val viewModel: PlayerViewModel by viewModel()
    private lateinit var playButton: ImageButton
    private lateinit var currentTrackTime: TextView
    private lateinit var track: Track
    private var mainThreadHandler: Handler? = null

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
        setViewModelObservers()
        preparePlayer()
        setOnClickListeners()

    }

    private fun setData() {
        track = intent.getSerializableExtra("Track") as Track

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
    }

    private fun setViewModelObservers() {
        with (viewModel) {
            observePlayerState().observe(this@AudioPlayerActivity) {
                when (it) {
                    PlayerViewModel.PlayerState.DEFAULT -> Unit
                    PlayerViewModel.PlayerState.PREPARED -> playButton.isEnabled = true
                    PlayerViewModel.PlayerState.COMPLETED -> setPlayButtonImage()
                }
            }

            observePlaying().observe(this@AudioPlayerActivity) {
                setPlayButtonImage()
            }

            observeProgressTime().observe(this@AudioPlayerActivity) {
                currentTrackTime.text = it
            }
        }
    }

    private fun preparePlayer() {
        viewModel.preparePlayer(
            track.previewUrl,
            mainThreadHandler,
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
            viewModel.playbackControl()
        }
    }

    private fun setPlayButtonImage() {
        playButton.setImageResource(
            if (viewModel.observePlaying().value == true){
                R.drawable.ic_pause
            } else R.drawable.ic_play
        )
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }
}