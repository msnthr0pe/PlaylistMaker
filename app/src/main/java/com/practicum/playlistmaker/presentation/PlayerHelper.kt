package com.practicum.playlistmaker.presentation

import android.media.MediaPlayer
import android.os.Handler
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerHelper {
     private enum class PlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED,
    }


    private var playerState: PlayerState = PlayerState.DEFAULT
    private var _isPlaying: Boolean = false
    val isPlaying: Boolean
        get() = _isPlaying
    private var mediaPlayer = MediaPlayer()
    private var mainThreadHandler: Handler? = null


     fun preparePlayer(
         handler: Handler?,
         songUrl: String,
         onPreparedAction: () -> Unit,
         onCompletedAction: () -> Unit,
        ) {
         mainThreadHandler = handler
         mediaPlayer.setDataSource(songUrl)
         mediaPlayer.prepareAsync()
         mediaPlayer.setOnPreparedListener {
             onPreparedAction()
             playerState = PlayerState.PREPARED
         }
         mediaPlayer.setOnCompletionListener {
             playerState = PlayerState.PREPARED
             mediaPlayer.seekTo(0)
             _isPlaying = false
             onCompletedAction()
         }
    }

    private fun startPlayer(playbackPositionAction: (String) -> Unit) {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING

        mainThreadHandler?.postDelayed(
            object : Runnable {
                override fun run() {
                    val playbackPosition = SimpleDateFormat("m:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                    playbackPositionAction(playbackPosition)
                    mainThreadHandler?.postDelayed(
                        this,
                        PLAYBACK_PROGRESS_REFRESH_DELAY
                    )
                }
            },
            PLAYBACK_PROGRESS_REFRESH_DELAY
        )
    }

     private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

     fun playbackControl(playbackPositionAction: (String) -> Unit) {
         _isPlaying = !_isPlaying

         when(playerState) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer(playbackPositionAction)
            }
            PlayerState.DEFAULT -> Unit
        }
    }

    fun onPause() {
        _isPlaying = false
        pausePlayer()
    }

    fun onDestroy() {
        _isPlaying = false
        mediaPlayer.release()
    }

    companion object {
        private const val PLAYBACK_PROGRESS_REFRESH_DELAY = 300L
    }
}