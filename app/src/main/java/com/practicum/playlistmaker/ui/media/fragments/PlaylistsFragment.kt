package com.practicum.playlistmaker.ui.media.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.ui.media.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding: FragmentPlaylistsBinding get() = _binding!!
    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

}