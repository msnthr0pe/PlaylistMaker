package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchTrackAdapter(var tracks: List<Track>, val onItemClick: (Track) -> Unit) : RecyclerView.Adapter<SearchTrackAdapter.SearchTrackViewHolder>() {

    inner class SearchTrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackNameText: TextView = itemView.findViewById(R.id.search_song_title)
        val artistNameText: TextView = itemView.findViewById(R.id.search_artist_name)
        val trackTime: TextView = itemView.findViewById(R.id.search_song_duration)
        val artworkUrlImage: ImageView = itemView.findViewById(R.id.search_song_image)

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
        return  SearchTrackViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search, parent, false))
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