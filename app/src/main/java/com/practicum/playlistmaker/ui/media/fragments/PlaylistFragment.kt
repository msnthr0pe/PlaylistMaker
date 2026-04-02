package com.practicum.playlistmaker.ui.media.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.ui.root.RootActivity

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding: FragmentPlaylistBinding get() = _binding!!

    private val rootActivity by lazy { requireActivity() as RootActivity }

    private lateinit var playlist: Playlist

    companion object {

        private const val ARGS_PLAYLIST_ID = "playlist.id"
        private const val ARGS_PLAYLIST_NAME = "playlist.name"
        private const val ARGS_PLAYLIST_DESCRIPTION = "playlist.description"
        private const val ARGS_PLAYLIST_COVER = "playlist.cover"
        private const val ARGS_TRACK_AMOUNT = "playlist.trackAmount"

        fun createArgs(playlist: Playlist) = Bundle().apply {
            putInt(ARGS_PLAYLIST_ID, playlist.id)
            putString(ARGS_PLAYLIST_NAME, playlist.name)
            putString(ARGS_PLAYLIST_DESCRIPTION, playlist.description)
            putString(ARGS_PLAYLIST_COVER, playlist.coverUri)
            putInt(ARGS_TRACK_AMOUNT, playlist.tracksAmount)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        setOnClickListeners()
    }

    private fun setData() {
        playlist = getPlaylist()

        with(binding) {
            playlistName.text = playlist.name
            playlistDescription.text = playlist.description
            playlistDescription.isVisible = playlist.description.isNotEmpty()
            if (playlist.coverUri.isNotEmpty()) playlistCover.setImageURI(playlist.coverUri.toUri())
            playlistTrackCount.text = requireActivity().resources.getQuantityString(
                R.plurals.tracks_count,
                playlist.tracksAmount,
                playlist.tracksAmount,
            )
        }
    }

    private fun getPlaylist(): Playlist {
        return Playlist(
            arguments?.getInt(ARGS_PLAYLIST_ID) ?: 0,
            arguments?.getString(ARGS_PLAYLIST_NAME) ?: "",
            arguments?.getString(ARGS_PLAYLIST_DESCRIPTION) ?: "",
            arguments?.getString(ARGS_PLAYLIST_COVER) ?: "",
            arguments?.getInt(ARGS_TRACK_AMOUNT) ?: 0,
        )
    }

    private fun setOnClickListeners() {
        with (binding) {
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}