package com.practicum.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.PlaylistUtil
import com.practicum.playlistmaker.databinding.ItemSearchBinding
import com.practicum.playlistmaker.domain.models.Track

class SearchTrackAdapter(var tracks: List<Track>, val onItemClick: (Track) -> Unit) : RecyclerView.Adapter<SearchTrackAdapter.SearchTrackViewHolder>() {

    private lateinit var binding: ItemSearchBinding

    inner class SearchTrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackNameText: TextView = binding.searchSongTitle
        val artistNameText: TextView = binding.searchArtistName
        val trackTime: TextView = binding.searchSongDuration
        val artworkUrlImage: ImageView = binding.searchSongImage

        fun bind(track: Track) {
            trackNameText.text = track.trackName
            artistNameText.text = track.artistName
            trackTime.text = PlaylistUtil.getFormattedTime(track.trackTimeMillis)
            PlaylistUtil.loadPicInto(itemView.context, track.artworkUrl100, artworkUrlImage)
            itemView.setOnClickListener {
                onItemClick(track)
            }
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchTrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemSearchBinding.inflate(inflater, parent, false)
        return  SearchTrackViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: SearchTrackViewHolder,
        position: Int,
    ) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    fun updateData(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
}