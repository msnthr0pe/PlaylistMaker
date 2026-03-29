package com.practicum.playlistmaker.ui.media

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.PlaylistUtil
import com.practicum.playlistmaker.databinding.ItemPlaylistBinding
import com.practicum.playlistmaker.domain.models.Playlist

class PlaylistAdapter(var playlists: List<Playlist>) : RecyclerView.Adapter<PlaylistAdapter.SearchTrackViewHolder>() {

    private lateinit var binding: ItemPlaylistBinding

    inner class SearchTrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val trackNameText: TextView = binding.searchSongTitle
//        val artistNameText: TextView = binding.searchArtistName
//        val trackTime: TextView = binding.searchSongDuration
        val artworkUrlImage: ImageView = binding.playlistCover

        fun bind(playlist: Playlist) {
//            trackNameText.text = track.trackName
//            artistNameText.text = track.artistName
//            trackTime.text = PlaylistUtil.getFormattedTime(track.trackTimeMillis)
//            PlaylistUtil.loadPicInto(itemView.context, track.artworkUrl100, artworkUrlImage)
//            itemView.setOnClickListener {
//                onItemClick(track)
//            }
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