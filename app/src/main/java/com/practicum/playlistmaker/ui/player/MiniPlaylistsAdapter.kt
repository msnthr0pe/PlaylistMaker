package com.practicum.playlistmaker.ui.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemMiniPlaylistBinding
import com.practicum.playlistmaker.domain.models.Playlist

class MiniPlaylistsAdapter(var playlists: List<Playlist>) : RecyclerView.Adapter<MiniPlaylistsAdapter.SearchTrackViewHolder>() {

    private lateinit var binding: ItemMiniPlaylistBinding

    inner class SearchTrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = binding.playlistMiniTitle
        val trackCount: TextView = binding.playlistMiniTrackAmount
        val image: ImageView = binding.playlistMiniCover

        fun bind(playlist: Playlist) {
            title.text = playlist.name
            trackCount.text = itemView.context.resources.getQuantityString(
                R.plurals.tracks_count,
                playlist.tracksAmount,
                playlist.tracksAmount,
            )
            if (playlist.coverUri.isNotEmpty()) {
                image.setImageURI(playlist.coverUri.toUri())
            } else {
                image.setImageResource(R.drawable.ic_placeholder)
            }
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchTrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemMiniPlaylistBinding.inflate(inflater, parent, false)
        return  SearchTrackViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: SearchTrackViewHolder,
        position: Int,
    ) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    fun updateData(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }
}