package com.practicum.playlistmaker.ui.player.viewmodel

import android.media.MediaPlayer
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val songUrl: String) : ViewModel() {

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
        handler: Handler?
    ) {
        mainThreadHandler = handler
        mediaPlayer.setDataSource(songUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState.PREPARED)
            internalPlayerState = InternalPlayerState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            internalPlayerState = InternalPlayerState.PREPARED
            mediaPlayer.seekTo(0)
            _isPlaying.postValue(false)
            playerStateLiveData.postValue(PlayerState.COMPLETED)
        }
    }

    private fun startPlayer(playbackPositionAction: (String) -> Unit) {
        mediaPlayer.start()
        internalPlayerState = InternalPlayerState.PLAYING

        mainThreadHandler?.postDelayed(
            object : Runnable {
                override fun run() {
                    val playbackPosition = SimpleDateFormat("m:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                    playbackPositionAction(playbackPosition)
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

    fun playbackControl(playbackPositionAction: (String) -> Unit) {
        _isPlaying.value?.let { _isPlaying.postValue(!it) }

        when(internalPlayerState) {
            InternalPlayerState.PLAYING -> {
                pausePlayer()
            }
            InternalPlayerState.PREPARED, InternalPlayerState.PAUSED -> {
                startPlayer(playbackPositionAction)
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

        fun getFactory(songUrl: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(songUrl)
            }
        }
    }
}