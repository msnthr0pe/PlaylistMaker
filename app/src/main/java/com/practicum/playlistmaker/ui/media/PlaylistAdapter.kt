package com.practicum.playlistmaker.ui.media

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemPlaylistBinding
import com.practicum.playlistmaker.domain.models.Playlist

class PlaylistAdapter(var playlists: List<Playlist>) : RecyclerView.Adapter<PlaylistAdapter.SearchTrackViewHolder>() {

    private lateinit var binding: ItemPlaylistBinding

    inner class SearchTrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = binding.playlistTitle
        val trackCount: TextView = binding.playlistTrackCount
        val image: ImageView = binding.playlistCover

        fun bind(playlist: Playlist) {
            title.text = playlist.name
            trackCount.text = playlist.tracksAmount.toString()
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
        binding = ItemPlaylistBinding.inflate(inflater, parent, false)
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