package com.practicum.playlistmaker.ui.media.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practicum.playlistmaker.databinding.FragmentFavouritesBinding
import com.practicum.playlistmaker.ui.media.viewmodel.FavouritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue


class FavouritesFragment : Fragment() {
    private var _binding: FragmentFavouritesBinding? = null
    private val binding: FragmentFavouritesBinding get() = _binding!!
    private val viewModel: FavouritesViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = FavouritesFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}