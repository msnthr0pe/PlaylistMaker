package com.practicum.playlistmaker.ui.media.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.PermissionHelper
import com.practicum.playlistmaker.PlaylistUtil
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentEditPlaylistBinding
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.EditPlaylistModel
import com.practicum.playlistmaker.ui.media.viewmodel.EditPlaylistViewModel
import com.practicum.playlistmaker.ui.root.RootActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : Fragment() {

    private var _binding: FragmentEditPlaylistBinding? = null
    private val binding: FragmentEditPlaylistBinding get() = _binding!!
    private val rootActivity by lazy { requireActivity() as RootActivity }
    private val viewModel: EditPlaylistViewModel by viewModel()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
            if (uri != null) {
                binding.playlistCoverImage.isVisible = false
                binding.playlistCover.setImageURI(uri)
            }
        }
    private var imageUri: Uri? = null
    private var isClickAllowed = true
    private val permissionHelper: PermissionHelper = PermissionHelper(this)
    private var editablePlaylist: EditPlaylistModel? = null

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        private const val ARGS_COVER_ID = "playlist.id"
        private const val ARGS_COVER_URI = "playlist.coverUri"
        private const val ARGS_PLAYLIST_NAME = "playlist.name"
        private const val ARGS_PLAYLIST_DESCRIPTION = "playlist.description"
        private const val ARGS_TRACK_AMOUNT = "playlist.trackAmount"

        fun createArgs(playlist: Playlist) = Bundle().apply {
            putInt(ARGS_COVER_ID, playlist.id)
            putString(ARGS_COVER_URI, playlist.coverUri)
            putString(ARGS_PLAYLIST_NAME, playlist.name)
            putString(ARGS_PLAYLIST_DESCRIPTION, playlist.description)
            putInt(ARGS_TRACK_AMOUNT, playlist.tracksAmount)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentEditPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        setListeners()
        setUpObserver()
    }

    private fun setUpObserver() {
        viewModel.observeUri().observe(viewLifecycleOwner) { uri ->
            uri?.let {
                sendUpdateToPlaylistFragment(it)
            }
        }
    }

    private fun getEditablePlaylist(): EditPlaylistModel? {
        val arguments = arguments
        return arguments?.let {EditPlaylistModel(
            arguments.getInt(ARGS_COVER_ID),
            arguments.getString(ARGS_COVER_URI)?.toUri(),
            arguments.getString(ARGS_PLAYLIST_NAME) ?: "",
            arguments.getString(ARGS_PLAYLIST_DESCRIPTION) ?: "",
            arguments.getInt(ARGS_TRACK_AMOUNT),
        )}
    }

    private fun setData() {
        editablePlaylist = getEditablePlaylist()

        val playlistData = editablePlaylist
        playlistData?.let {
            with(binding) {
                playlistCover.setImageURI(playlistData.coverUri)
                if (playlistData.coverUri.toString().isNotEmpty()) playlistCoverImage.isVisible = false
                playlistNameEt.setText(playlistData.name)
                playlistDescriptionEt.setText(playlistData.description)
                toolbarTitle.text = getString(R.string.edit_playlist)
                createPlaylistButton.apply {
                    text = getString(R.string.save_playlist)
                    isEnabled = playlistNameEt.text?.isNotBlank() == true
                }
            }
        }
    }

    private fun setListeners() {
        with(binding) {
            backBtn.setOnClickListener {
                handleAbortAttempt()
            }
            playlistNameEt.doOnTextChanged { text, _, _, _ ->
                createPlaylistButton.isEnabled = text?.isNotBlank() == true
            }
            createPlaylistButton.setOnClickListener {
                lifecycleScope.launch {
                    val name = playlistNameEt.text.toString()
                    val description = playlistDescriptionEt.text.toString()
                    val editablePlaylist = editablePlaylist
                    if (editablePlaylist == null) {
                        viewModel.createPlaylist(
                            name = name,
                            description = description,
                            coverUri = imageUri,
                        )
                        PlaylistUtil.showSnackbar(
                            rootView = binding.root,
                            message = getString(
                                R.string.new_playlist_snackbar_text,
                                name,
                            )
                        )
                        findNavController().popBackStack()
                    } else {
                        viewModel.updatePlaylist(
                            id = editablePlaylist.id,
                            name = name,
                            description = description,
                            coverUri = imageUri,
                        )
                        PlaylistUtil.showSnackbar(
                            rootView = binding.root,
                            message = getString(
                                R.string.edited_playlist_snackbar_text,
                                name,
                            )
                        )
                    }
                }
            }
        }
        binding.playlistCover.setOnClickListener {
            if (clickDebounce()) {
                selectCoverImage()
            }
        }

        rootActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleAbortAttempt()
        }
    }

    private fun sendUpdateToPlaylistFragment(coverUri: String) {
        val editablePlaylist = editablePlaylist
        if (editablePlaylist == null) return
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.playlistFragment, inclusive = true)
            .build()
        val name = binding.playlistNameEt.text.toString()
        val description = binding.playlistDescriptionEt.text.toString()
        findNavController().navigate(
            R.id.action_editPlaylistFragment_to_playlistFragment,
            PlaylistFragment.createArgs(
                id = editablePlaylist.id,
                name = name,
                description = description,
                coverUri = coverUri,
                tracksAmount = editablePlaylist.tracksAmount,
            ),
            navOptions,
        )
    }

    private fun selectCoverImage() {
        permissionHelper.checkAndRequestPermissions(
            onGranted = {
                pickMedia.launch("image/*")
            },
            onDenied = {
                PlaylistUtil.showSnackbar(binding.root, getString(R.string.no_permission_text))
            }
        )
    }

    private fun handleAbortAttempt() {
        if (hasUnsavedChanges() && editablePlaylist == null) {
            PlaylistUtil.showAlertDialog(
                context = rootActivity,
                title = getString(R.string.abort_playlist_creation_question),
                message = getString(R.string.abort_playlist_creation_message),
                negativeBtnTitle = getString(R.string.cancel),
                positiveBtnTitle = getString(R.string.end),
                negativeBtnAction = {},
                positiveBtnAction = { findNavController().popBackStack() },
            )
        } else {
            findNavController().popBackStack()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        return imageUri != null ||
                binding.playlistNameEt.text?.isNotEmpty() == true ||
                binding.playlistDescriptionEt.text?.isNotEmpty() == true
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetUri()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}