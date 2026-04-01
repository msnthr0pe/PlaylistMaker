package com.practicum.playlistmaker.ui.media.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.ui.media.PlaylistAdapter
import com.practicum.playlistmaker.ui.media.viewmodel.PlaylistsViewModel
import com.practicum.playlistmaker.ui.root.RootActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding: FragmentPlaylistsBinding get() = _binding!!
    private val rootActivity by lazy { requireActivity() as RootActivity }
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlaylistAdapter
    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModelObserver()
        setClickListeners()
        setupRecyclerView()
        updatePlaylists()
    }

    private fun setupViewModelObserver() {
        viewModel.observePlaylists().observe(viewLifecycleOwner) { state ->
            adapter.updateData(state.playlists)
            updateEmptyPlaceholderVisibility()
        }
    }

    private fun setClickListeners() {
        binding.newPlaylistButton.setOnClickListener {
            rootActivity.setBottomBarVisibility(false)
            findNavController().navigate(R.id.action_mediaFragment_to_addPlaylistFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = PlaylistAdapter(emptyList()) { playlist ->
            rootActivity.setBottomBarVisibility(false)
            findNavController().navigate(
                R.id.action_mediaFragment_to_playlistFragment,
                PlaylistFragment.createArgs(playlist),
            )
        }

        recyclerView = binding.playlistRecycler
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter
        updateEmptyPlaceholderVisibility()
    }

    private fun updateEmptyPlaceholderVisibility() {
        with(binding) {
            playlistsPlaceholder.isVisible = adapter.playlists.isEmpty()
            recyclerView.isVisible = !adapter.playlists.isEmpty()
        }
    }

    private fun updatePlaylists() {
        viewModel.getPlaylists()
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}