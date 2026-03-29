package com.practicum.playlistmaker.ui.media.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.PlaylistUtil
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAddPlaylistBinding
import com.practicum.playlistmaker.ui.media.viewmodel.AddPlaylistViewModel
import com.practicum.playlistmaker.ui.root.RootActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddPlaylistFragment : Fragment() {

    private var _binding: FragmentAddPlaylistBinding? = null
    private val binding: FragmentAddPlaylistBinding get() = _binding!!
    private val rootActivity by lazy { requireActivity() as RootActivity }
    private val viewModel: AddPlaylistViewModel by viewModel()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            imageUri = uri
            if (uri != null) {
                binding.playlistCoverImage.isVisible = false
                binding.playlistCover.setImageURI(uri)
            }

        }
    private var imageUri: Uri? = null
    private var isClickAllowed = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAddPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()

    }

    private fun setListeners() {
        with(binding) {
            backBtn.setOnClickListener {
                handleAbortAttempt()
            }
            playlistNameEt.doOnTextChanged { text, _, _, _ ->
                if (text?.isNotEmpty() == true) {
                    createPlaylistButton.isEnabled = true
                }

            }
            createPlaylistButton.setOnClickListener {
                lifecycleScope.launch {
                    val name = playlistNameEt.text.toString()
                    viewModel.createPlaylist(
                        name = name,
                        description = playlistDescriptionEt.text.toString(),
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
                }
            }
        }
        binding.playlistCover.setOnClickListener {
            if (clickDebounce()) {
                pickMedia.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }

        rootActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleAbortAttempt()
        }
    }

    private fun handleAbortAttempt() {
        if (hasUnsavedChanges()) {
            PlaylistUtil.showAlertDialog(
                context = rootActivity,
                title = getString(R.string.abort_playlist_creation_question),
                message = getString(R.string.abort_playlist_creation_message),
                negativeBtnTitle = getString(R.string.no),
                positiveBtnTitle = getString(R.string.yes),
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}