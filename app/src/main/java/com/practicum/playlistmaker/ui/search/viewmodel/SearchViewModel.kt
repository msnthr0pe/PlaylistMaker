package com.practicum.playlistmaker.ui.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.search.TracksInteractor
import com.practicum.playlistmaker.domain.search.history.SearchHistoryInteractor
import com.practicum.playlistmaker.ui.search.models.PlaceholdersState
import com.practicum.playlistmaker.ui.search.models.SearchState
import kotlin.collections.isNotEmpty

class SearchViewModel(
    private val historyInteractor: SearchHistoryInteractor,
) : ViewModel() {
    private val displayedTracks = MutableLiveData<List<Track>>(emptyList())
    val observeDisplayedTracks: LiveData<List<Track>> = displayedTracks
    var history = ArrayList<Track>()

    private val searchState = MutableLiveData(
        SearchState(
            PlaceholdersState
                (
                searchPlaceholderVisible = false,
                noInternetPlaceholderVisible = false
                        ),
            false,
        )
    )
    val observeSearchState: LiveData<SearchState> = searchState

    private var isHistory= false
    private var placeholders = PlaceholdersState(false, false)

    fun getTracks(query: String, callback: (List<Track>?) -> Unit) {
        val tracksInteractor = Creator.provideTracksInteractor()
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
            displayedTracks.postValue(tracks)
            return
        }
        update {
            displayedTracks.postValue(it)
        }
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

    fun showHistory(ifHistory: () -> Unit) {
        var history: ArrayList<Track>? = null
        update { history = it }
        if (history != null && history.isNotEmpty()) {
            updateDisplayedTracks(history)

            ifHistory()
        } else {
            updateDisplayedTracks(arrayListOf())
        }
    }

     fun loadTracks(lastSearchQuery: String, onFound: () -> Unit) {
        getTracks(
            lastSearchQuery,
        ) { foundTracks ->
            onFound()

            if (foundTracks == null) {
                onFound()
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
        searchState.postValue(
            SearchState(
                placeholders,
                isHistory,
            )
        )
    }

    fun updatePlaceholdersState(search: Boolean, noInternet: Boolean) {
        placeholders = PlaceholdersState(
            search,
            noInternet
        )
        searchState.postValue(
            SearchState(
                placeholders,
                isHistory,
            )
        )
    }

    companion object {
        fun getFactory(historyInteractor: SearchHistoryInteractor):
                ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(historyInteractor)
            }
        }
    }
}