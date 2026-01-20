package com.practicum.playlistmaker.ui.player.viewmodel

import android.media.MediaPlayer
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel() : ViewModel() {

    private enum class InternalPlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED,
    }

    enum class PlayerState {
        DEFAULT,
        PREPARED,
        COMPLETED,
    }

    private val playerStateLiveData = MutableLiveData(PlayerState.DEFAULT)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData("0:00")
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private var internalPlayerState: InternalPlayerState = InternalPlayerState.DEFAULT
    private val _isPlaying = MutableLiveData<Boolean>(false)
    fun observePlaying(): LiveData<Boolean> = _isPlaying
    private var mediaPlayer = MediaPlayer()
    private var mainThreadHandler: Handler? = null

    fun preparePlayer(
        songUrl: String,
        handler: Handler?,
        onPreparedAction: () -> Unit,
        onCompletedAction: () -> Unit,
    ) {
        mainThreadHandler = handler
        mediaPlayer.setDataSource(songUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPreparedAction()
            internalPlayerState = InternalPlayerState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            internalPlayerState = InternalPlayerState.PREPARED
            mediaPlayer.seekTo(0)
            _isPlaying.postValue(false)
            onCompletedAction()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        internalPlayerState = InternalPlayerState.PLAYING

        mainThreadHandler?.postDelayed(
            object : Runnable {
                override fun run() {
                    val playbackPosition = SimpleDateFormat("m:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                    progressTimeLiveData.postValue(playbackPosition)
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
        internalPlayerState = InternalPlayerState.PAUSED
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

    fun playbackControl() {
        _isPlaying.value?.let { _isPlaying.postValue(!it) }

        when(internalPlayerState) {
            InternalPlayerState.PLAYING -> {
                pausePlayer()
            }
            InternalPlayerState.PREPARED, InternalPlayerState.PAUSED -> {
                startPlayer()
            }
            InternalPlayerState.DEFAULT -> Unit
        }
    }

    fun onPause() {
        _isPlaying.postValue(false)
        pausePlayer()
    }

    fun onDestroy() {
        _isPlaying.postValue(false)
        mediaPlayer.release()
    }

    companion object {
        private const val PLAYBACK_PROGRESS_REFRESH_DELAY = 300L
    }
}