package com.practicum.playlistmaker.ui.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.domain.search.history.SearchHistoryInteractor
import com.practicum.playlistmaker.ui.search.models.SearchState
import com.practicum.playlistmaker.ui.search.models.PlaceholdersState
import kotlin.collections.isNotEmpty

class SearchViewModel(
    private val historyInteractor: SearchHistoryInteractor,
    private val tracksInteractor: TracksInteractor,
) : ViewModel() {

    private var history = ArrayList<Track>()

    private var tracks = ArrayList<Track>()
    private val searchState = MutableLiveData(
        SearchState(
            tracks,
            PlaceholdersState
                (
                searchPlaceholderVisible = false,
                noInternetPlaceholderVisible = false
            ),
            false,
        )
    )
    val observeSearchState: LiveData<SearchState> = searchState

    private var isHistory = false
    private var placeholders = PlaceholdersState(
        searchPlaceholderVisible = false,
        noInternetPlaceholderVisible = false
    )

    fun getTracks(query: String, callback: (List<Track>?) -> Unit) {
        tracksInteractor.searchForTracks(
            expression = query,
            object : TracksInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>?) {
                    callback(foundTracks)
                }
            }
        )
    }

    fun updateDisplayedTracks(tracks: ArrayList<Track>? = null) {
        if (tracks != null) {
            this.tracks = tracks
        } else {
            update {
                this.tracks = it
            }
        }
        postSearchState()
    }

    private fun postSearchState() {
        searchState.postValue(
            SearchState(
                tracks,
                placeholders,
                isHistory
            )
        )
    }

    fun updateCurrentHistory() {
        update {
            history = it
        }
    }

    private fun update(callback: (ArrayList<Track>) -> Unit){
        historyInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer {
            override fun consume(searchHistory: ArrayList<Track>?) {
                callback(searchHistory ?: arrayListOf())
            }
        })
    }

    fun addHistory(tracks: ArrayList<Track>? = null) {
        if (tracks == null) {
            historyInteractor.saveToHistory(history)
            return
        }
        historyInteractor.saveToHistory(tracks)
    }

    fun addToHistory(track: Track) {
        history.add(track)
    }

    fun showHistory() {
        var history: ArrayList<Track>? = null
        update { history = it }
        if (history != null && history.isNotEmpty()) {
            updateDisplayedTracks(history)
        } else {
            updateDisplayedTracks(arrayListOf())
        }
    }

     fun loadTracks(lastSearchQuery: String) {
        getTracks(
            lastSearchQuery,
        ) { foundTracks ->

            if (foundTracks == null) {
                updatePlaceholdersState(search = false, noInternet = true)

                updateDisplayedTracks(arrayListOf())
                return@getTracks
            }

            if (foundTracks.isNotEmpty()) {
                updateDisplayedTracks(ArrayList(foundTracks))
            } else {
                updatePlaceholdersState(search = true, noInternet = false)
                updateDisplayedTracks(arrayListOf())
            }
        }
    }

    fun updateHistoryEnablement(isEnabled: Boolean) {
        isHistory = isEnabled
        postSearchState()
    }

    fun updatePlaceholdersState(search: Boolean, noInternet: Boolean) {
        placeholders = PlaceholdersState(
            search,
            noInternet
        )
        postSearchState()
    }
}