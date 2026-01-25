package com.practicum.playlistmaker.ui.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.PlaylistUtil
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.player.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private val viewModel: PlayerViewModel by viewModel()
    private lateinit var playButton: ImageButton
    private lateinit var currentTrackTime: TextView
    private lateinit var track: Track
    private var mainThreadHandler: Handler? = null

    companion object {

        private const val ARGS_TRACK_TRACK_NAME = "track.trackName"
        private const val ARGS_TRACK_ARTIST_NAME = "track.artistName"
        private const val ARGS_TRACK_TRACK_TIME_MILLIS = "track.trackTimeMillis"
        private const val ARGS_TRACK_ARTWORK_URL_100 = "track.artworkUrl100"
        private const val ARGS_TRACK_COLLECTION_NAME = "track.collectionName"
        private const val ARGS_TRACK_RELEASE_DATE = "track.releaseDate"
        private const val ARGS_TRACK_PRIMARY_GENRE_NAME = "track.primaryGenreName"
        private const val ARGS_TRACK_COUNTRY = "track.country"
        private const val ARGS_TRACK_PREVIEW_URL = "track.previewUrl"

        const val TAG = "AudioPlayerFragment"

        fun newInstance(track: Track): Fragment {
            return AudioPlayerFragment().apply {
                arguments = bundleOf(
                    ARGS_TRACK_TRACK_NAME to track.trackName,
                    ARGS_TRACK_ARTIST_NAME to track.artistName,
                    ARGS_TRACK_TRACK_TIME_MILLIS to track.trackTimeMillis,
                    ARGS_TRACK_ARTWORK_URL_100 to track.artworkUrl100,
                    ARGS_TRACK_COLLECTION_NAME to track.collectionName,
                    ARGS_TRACK_RELEASE_DATE to track.releaseDate,
                    ARGS_TRACK_PRIMARY_GENRE_NAME to track.primaryGenreName,
                    ARGS_TRACK_COUNTRY to track.country,
                    ARGS_TRACK_PREVIEW_URL to track.previewUrl,
                )
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    private fun getTrack(): Track {
        return Track(
            requireArguments().getString(ARGS_TRACK_TRACK_NAME) ?: "",
            requireArguments().getString(ARGS_TRACK_ARTIST_NAME) ?: "",
            requireArguments().getLong(ARGS_TRACK_TRACK_TIME_MILLIS),
            requireArguments().getString(ARGS_TRACK_ARTWORK_URL_100) ?: "",

            requireArguments().getString(ARGS_TRACK_COLLECTION_NAME) ?: "",

            requireArguments().getString(ARGS_TRACK_RELEASE_DATE) ?: "",

            requireArguments().getString(ARGS_TRACK_PRIMARY_GENRE_NAME) ?: "",
            requireArguments().getString(ARGS_TRACK_COUNTRY) ?: "",
            requireArguments().getString(ARGS_TRACK_PREVIEW_URL) ?: "",
        )
    }

    private fun setData() {
        track = getTrack()

        val pic = PlaylistUtil.getHigherResolutionPic(track.artworkUrl100)
        val imageView = binding.playerSongImage
        PlaylistUtil.loadPicInto(requireContext(), pic, imageView)

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
            observePlayerState().observe(viewLifecycleOwner) {
                when (it) {
                    PlayerViewModel.PlayerState.DEFAULT -> Unit
                    PlayerViewModel.PlayerState.PREPARED -> playButton.isEnabled = true
                    PlayerViewModel.PlayerState.COMPLETED -> setPlayButtonImage()
                }
            }

            observePlaying().observe(viewLifecycleOwner) {
                setPlayButtonImage()
            }

            observeProgressTime().observe(viewLifecycleOwner) {
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
                //finish()
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