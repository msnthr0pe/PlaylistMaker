package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class SearchTrackAdapter(var tracks: List<Track>) : RecyclerView.Adapter<SearchTrackAdapter.SearchTrackViewHolder>() {

    inner class SearchTrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackNameText: TextView = itemView.findViewById(R.id.search_song_title)
        val artistNameText: TextView = itemView.findViewById(R.id.search_artist_name)
        val trackTime: TextView = itemView.findViewById(R.id.search_song_duration)
        val artworkUrlImage: ImageView = itemView.findViewById(R.id.search_song_image)

        fun bind(track: Track) {
            trackNameText.text = track.trackName
            artistNameText.text = track.artistName
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .centerCrop()
                .transform(RoundedCorners(6))
                .placeholder(R.drawable.ic_placeholder)
                .into(artworkUrlImage)
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