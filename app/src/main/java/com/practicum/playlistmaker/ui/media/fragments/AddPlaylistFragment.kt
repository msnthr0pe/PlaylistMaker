package com.practicum.playlistmaker.ui.media.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.PlaylistUtil
import com.practicum.playlistmaker.databinding.FragmentAddPlaylistBinding
import com.practicum.playlistmaker.ui.media.viewmodel.AddPlaylistViewModel
import com.practicum.playlistmaker.ui.root.RootActivity
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
                rootActivity.setBottomBarVisibility(true)
                findNavController().popBackStack()
            }
            playlistNameEt.doOnTextChanged { text, _, _, _ ->
                createPlaylistButton.isEnabled = text?.isNotEmpty() == true
            }
            createPlaylistButton.setOnClickListener {
                lifecycleScope.launch {
                    val name = playlistNameEt.text.toString()
                    viewModel.createPlaylist(
                        name = name,
                        description = playlistDescriptionEt.text.toString(),
                        coverUri = imageUri,
                    )
                    PlaylistUtil.showSnackbar(binding.root, "Плейлист $name успешно создан")
                    findNavController().popBackStack()
                }
            }
        }
        binding.playlistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}