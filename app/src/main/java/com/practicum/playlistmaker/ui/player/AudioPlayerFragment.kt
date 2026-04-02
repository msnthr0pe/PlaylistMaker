package com.practicum.playlistmaker.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.PlaylistUtil
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.media.models.PlayerEvent
import com.practicum.playlistmaker.ui.media.viewmodel.PlaylistsViewModel
import com.practicum.playlistmaker.ui.player.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

    private lateinit var binding: FragmentAudioPlayerBinding

    private val playerViewModel: PlayerViewModel by viewModel()
    private val playListsViewModel: PlaylistsViewModel by viewModel()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MiniPlaylistsAdapter
    private lateinit var playButton: ImageButton
    private lateinit var playlistBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var currentTrackTime: TextView
    private lateinit var track: Track

    companion object {

        private const val ARGS_TRACK_TRACK_ID = "track.trackId"
        private const val ARGS_TRACK_TRACK_NAME = "track.trackName"
        private const val ARGS_TRACK_ARTIST_NAME = "track.artistName"
        private const val ARGS_TRACK_TRACK_TIME_MILLIS = "track.trackTimeMillis"
        private const val ARGS_TRACK_ARTWORK_URL_100 = "track.artworkUrl100"
        private const val ARGS_TRACK_COLLECTION_NAME = "track.collectionName"
        private const val ARGS_TRACK_RELEASE_DATE = "track.releaseDate"
        private const val ARGS_TRACK_PRIMARY_GENRE_NAME = "track.primaryGenreName"
        private const val ARGS_TRACK_COUNTRY = "track.country"
        private const val ARGS_TRACK_PREVIEW_URL = "track.previewUrl"

        fun createArgs(track: Track) = Bundle().apply {
            putLong(ARGS_TRACK_TRACK_ID, track.trackId)
            putString(ARGS_TRACK_TRACK_NAME, track.trackName)
            putString(ARGS_TRACK_ARTIST_NAME, track.artistName)
            putLong(ARGS_TRACK_TRACK_TIME_MILLIS, track.trackTimeMillis)
            putString(ARGS_TRACK_ARTWORK_URL_100, track.artworkUrl100)
            putString(ARGS_TRACK_COLLECTION_NAME, track.collectionName)
            putString(ARGS_TRACK_RELEASE_DATE, track.releaseDate)
            putString(ARGS_TRACK_PRIMARY_GENRE_NAME, track.primaryGenreName)
            putString(ARGS_TRACK_COUNTRY, track.country)
            putString(ARGS_TRACK_PREVIEW_URL, track.previewUrl)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentAudioPlayerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()
        setupBottomSheet()
        setViewModelObservers()
        playListsViewModel.getPlaylists()
        preparePlayer()
        setOnClickListeners()
    }

    override fun onResume() {
        super.onResume()
        if (playlistBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            binding.scrim.visibility = View.VISIBLE
        }
        playerViewModel.setupFavouriteButtonState(track)
    }

    private fun getTrack(): Track {
        return Track(
            arguments?.getLong(ARGS_TRACK_TRACK_ID) ?: 0,
            arguments?.getString(ARGS_TRACK_TRACK_NAME) ?: "",
            arguments?.getString(ARGS_TRACK_ARTIST_NAME) ?: "",
            arguments?.getLong(ARGS_TRACK_TRACK_TIME_MILLIS) ?: 0,
            arguments?.getString(ARGS_TRACK_ARTWORK_URL_100) ?: "",

            arguments?.getString(ARGS_TRACK_COLLECTION_NAME) ?: "",

            arguments?.getString(ARGS_TRACK_RELEASE_DATE) ?: "",

            arguments?.getString(ARGS_TRACK_PRIMARY_GENRE_NAME) ?: "",
            arguments?.getString(ARGS_TRACK_COUNTRY) ?: "",
            arguments?.getString(ARGS_TRACK_PREVIEW_URL) ?: "",
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
        with (playerViewModel) {
            observeAudioPlayerState().observe(viewLifecycleOwner) {
                setPlayButtonImage()
                currentTrackTime.text = it.progressTime
                if (it.favouriteButtonState.shouldUpdateFavourites) {
                    track.isFavourite = it.favouriteButtonState.isFavourite
                    updateFavouriteButtonState()
                }
            }
        }
        with(playListsViewModel) {
            observePlaylists().observe(viewLifecycleOwner) { state ->
                adapter.updateData(state.playlists)
                when (state?.playerEvent) {
                    is PlayerEvent.TrackAddSuccess -> {
                        playlistBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                        PlaylistUtil.showSnackbar(
                            binding.root,
                            requireActivity().getString(
                                R.string.new_track_in_playlist_snackbar_text,
                                state.playerEvent.playlistName
                            ),
                            PlaylistUtil.SnackbarLayoutTypes.COORDINATOR_LAYOUT,
                        )
                    }

                    is PlayerEvent.TrackAddDuplicate -> PlaylistUtil.showSnackbar(
                        binding.root,
                        requireActivity().getString(R.string.track_exists_in_playlist_snackbar_text, state.playerEvent.playlistName),
                        PlaylistUtil.SnackbarLayoutTypes.COORDINATOR_LAYOUT,
                        )
                    null -> Unit
                }
            }
        }
    }

    private fun updateFavouriteButtonState() {
        val imageResource = if (track.isFavourite) {
            R.drawable.ic_favorites_filled
        } else {
            R.drawable.ic_favorites
        }
        binding.favoritesButton.setImageResource(imageResource)
    }

    private fun preparePlayer() {
        playerViewModel.preparePlayer(
            track.previewUrl,
            {
                playButton.isEnabled = true
            },
            {
                setPlayButtonImage()
            })
    }

    private fun setupBottomSheet() {

        playlistBottomSheetBehavior = BottomSheetBehavior.from(binding.playerBottomSheet)

        with(playlistBottomSheetBehavior) {
            peekHeight = 0

            binding.scrim.setOnClickListener {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
            state = BottomSheetBehavior.STATE_HIDDEN

            skipCollapsed = true

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    val visible = newState != BottomSheetBehavior.STATE_HIDDEN
                    binding.scrim.visibility = if (visible) View.VISIBLE else View.GONE
                    if (!visible) binding.scrim.alpha = 0f
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.scrim.alpha = 0.6f * slideOffset.coerceIn(0f, 1f)
                }
            })
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = MiniPlaylistsAdapter(emptyList()) { playlist ->
            playListsViewModel.addToPlaylist(track, playlist)
        }

        recyclerView = binding.miniPlaylistsRecycler
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setOnClickListeners() {
        binding.apply {
            backPlayerBtn.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        playButton = binding.playButton
        playButton.setOnClickListener {
            playerViewModel.playbackControl()
        }
        binding.favoritesButton.setOnClickListener {
            track.isFavourite = !track.isFavourite
            updateFavouriteButtonState()
            playerViewModel.onFavouritesButtonClicked(track)
        }
        binding.addToPlaylistButton.setOnClickListener {
            playlistBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.newPlaylistButton.setOnClickListener {
            playerViewModel.resetPlayer()
            findNavController().navigate(R.id.action_audioPlayerFragment_to_addPlaylistFragment)
        }
    }

    private fun setPlayButtonImage() {
        playButton.setImageResource(
            if (playerViewModel.observeAudioPlayerState().value?.isPlaying == true){
                R.drawable.ic_pause
            } else R.drawable.ic_play
        )
    }

    override fun onPause() {
        super.onPause()
        playListsViewModel.resetSnackbarState()
        playerViewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.onDestroy()
    }
}