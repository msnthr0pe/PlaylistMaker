package com.practicum.playlistmaker.ui.player.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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
    private val _isPlaying = MutableLiveData(false)
    fun observePlaying(): LiveData<Boolean> = _isPlaying
    private var mediaPlayer = MediaPlayer()
    private var playerJob: Job? = null

    fun preparePlayer(
        songUrl: String,
        onPreparedAction: () -> Unit,
        onCompletedAction: () -> Unit,
    ) {
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

        playerJob = viewModelScope.launch {
            while (isActive) {
                val playbackPosition = SimpleDateFormat("m:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                progressTimeLiveData.postValue(playbackPosition)
                delay(PLAYBACK_PROGRESS_REFRESH_DELAY)
            }
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        internalPlayerState = InternalPlayerState.PAUSED
        playerJob?.cancel()
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