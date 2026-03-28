package com.practicum.playlistmaker.ui.media.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavouritesBinding
import com.practicum.playlistmaker.ui.media.viewmodel.FavouritesViewModel
import com.practicum.playlistmaker.ui.player.AudioPlayerFragment
import com.practicum.playlistmaker.ui.root.RootActivity
import com.practicum.playlistmaker.ui.search.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue


class FavouritesFragment : Fragment() {
    private var _binding: FragmentFavouritesBinding? = null
    private val binding: FragmentFavouritesBinding get() = _binding!!
    private val viewModel: FavouritesViewModel by viewModel()
    private lateinit var placeholder: LinearLayout
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: TrackAdapter
    private val rootActivity by lazy { requireActivity() as RootActivity }
    private var isClickAllowed = true



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObservers()
        setupRecycler()
    }

    private fun registerObservers() {
        with(viewModel) {
            observeFavourites().observe(viewLifecycleOwner) { tracks ->
                adapter.updateData(tracks)
                if (tracks.isEmpty()) {
                    shouldShowPlaceholder(true)
                } else {
                    shouldShowPlaceholder(false)
                }
            }
        }
    }

    private fun setupRecycler() {
        placeholder = binding.favouritesPlaceholderLayout
        recycler = binding.favouritesRecycler

        viewModel.initLoadFavourites()
        adapter = TrackAdapter(emptyList()) {
            if (clickDebounce()) {

                rootActivity.setBottomBarVisibility(false)

                findNavController().navigate(R.id.action_mediaFragment_to_audioPlayerFragment,
                    AudioPlayerFragment.createArgs(it))

            }
        }
        recycler.adapter = adapter
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

    private fun shouldShowPlaceholder(show: Boolean) {
        placeholder.isVisible = show
        recycler.isVisible = !show
    }

    companion object {
        fun newInstance() = FavouritesFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}