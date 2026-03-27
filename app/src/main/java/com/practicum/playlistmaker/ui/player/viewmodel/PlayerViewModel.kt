package com.practicum.playlistmaker.ui.player.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.db.FavouritesInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.player.models.AudioPlayerModel
import com.practicum.playlistmaker.ui.player.models.FavouriteState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val favouritesInteractor: FavouritesInteractor,
) : ViewModel() {

    private enum class InternalPlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED,
    }

    private var progressTime = DEFAULT_PROGRESS_TIME
    private var favouriteButtonState = FavouriteState(
        shouldUpdateFavourites = true,
        isFavourite = false,
    )
    private var isPlaying = false

    private val _audioPlayerState = MutableLiveData(
    AudioPlayerModel(
            progressTime,
            favouriteButtonState,
            isPlaying,
        )
    )
    fun observeAudioPlayerState(): LiveData<AudioPlayerModel> = _audioPlayerState

    private var internalPlayerState: InternalPlayerState = InternalPlayerState.DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var playerJob: Job? = null

    fun setupFavouriteButtonState(track: Track) {
        viewModelScope.launch {
            favouritesInteractor.getFavourites().collect {
                favouriteButtonState = FavouriteState(
                    shouldUpdateFavourites = true,
                    isFavourite = it.contains(track),
                )
                postAudioPlayerState()
            }
        }
    }

    fun onFavouritesButtonClicked(track: Track) {
        viewModelScope.launch {
            if (!track.isFavourite) {
                favouritesInteractor.removeFavourite(track)
            } else {
                favouritesInteractor.addFavourite(track)
            }
        }
    }

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
            isPlaying = false
            postAudioPlayerState()
            onCompletedAction()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        internalPlayerState = InternalPlayerState.PLAYING

        playerJob = viewModelScope.launch {
            while (isActive) {
                val playbackPosition = SimpleDateFormat("m:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                progressTime = playbackPosition
                postAudioPlayerState()
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
        isPlaying = !isPlaying
        postAudioPlayerState()

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
        isPlaying = false
        postAudioPlayerState()
        pausePlayer()
    }

    fun onDestroy() {
        isPlaying = false
        postAudioPlayerState()
        mediaPlayer.release()
    }

    fun postAudioPlayerState() {
        _audioPlayerState.postValue(
            AudioPlayerModel(
                progressTime,
                favouriteButtonState,
                isPlaying,
            )
        )
        favouriteButtonState = FavouriteState(
            shouldUpdateFavourites = false,
            isFavourite = favouriteButtonState.isFavourite,
        )
    }

    companion object {
        private const val PLAYBACK_PROGRESS_REFRESH_DELAY = 300L
        private const val DEFAULT_PROGRESS_TIME = "0:00"
    }
}