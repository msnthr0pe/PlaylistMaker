package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AudioPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setData()
        findViewById<ImageView>(R.id.back_player_btn).setOnClickListener {
            finish()
        }

    }

    private fun setData() {
        val track = intent.getSerializableExtra("Track") as Track
        val pic = PlaylistUtil.getHigherResolutionPic(track.artworkUrl100)
        val imageView = findViewById<ImageView>(R.id.player_song_image)
        PlaylistUtil.loadPicInto(this, pic, imageView)
        findViewById<TextView>(R.id.player_song_title).text = track.trackName
        findViewById<TextView>(R.id.player_artist).text = track.artistName
        findViewById<TextView>(R.id.duration_time).text = PlaylistUtil.getFormattedTime(track.trackTimeMillis)
        findViewById<TextView>(R.id.collection_name).text = track.collectionName
        findViewById<TextView>(R.id.song_year).text = track.releaseDate.substring(0, 4)
        findViewById<TextView>(R.id.genre_name).text = track.primaryGenreName
        findViewById<TextView>(R.id.country_name).text = track.country
    }
}