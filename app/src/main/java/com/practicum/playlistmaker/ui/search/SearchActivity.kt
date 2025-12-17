package com.practicum.playlistmaker.ui.search

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.HISTORY_PREFS_KEY
import com.practicum.playlistmaker.HISTORY_PREFS_NAME
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.SearchHistory
import com.practicum.playlistmaker.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.player.AudioPlayerActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private var searchText = ""
    private var lastSearchQuery = ""
    private lateinit var adapter: SearchTrackAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var searchPlaceholderLayout: LinearLayout
    private lateinit var noInternetPlaceholderLayout: LinearLayout
    private lateinit var searchProgressBar: ProgressBar
    private val history by lazy { SearchHistory() }
    private val historyPrefs by lazy { getSharedPreferences(HISTORY_PREFS_NAME, MODE_PRIVATE) }
    private var currentHistory: ArrayList<Track> = arrayListOf()
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch() }
    private var isClickAllowed = true


    private val prefsChangeListener by lazy {
        SharedPreferences.OnSharedPreferenceChangeListener{ prefs, key ->
            if (key == HISTORY_PREFS_KEY) {
                adapter.updateData(history.readHistory(prefs) ?: emptyList())
                setRecyclerHeight(true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecycler()

        searchProgressBar = findViewById(R.id.search_progress_bar)


        val searchField = findViewById<EditText>(R.id.search_et)
        val clearBtn = findViewById<ImageView>(R.id.clear_text_btn)

        clearBtn.setOnClickListener {
            searchField.setText("")
            searchText = ""

            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        clearBtn.visibility = View.GONE

        findViewById<ImageView>(R.id.back_search_btn).setOnClickListener {
            finish()
        }

        searchField.doOnTextChanged { _, _, _, _ ->
            if (searchField.text.isNotEmpty()) {
                clearBtn.isVisible = true
                hideHistory()
            } else {
                showHistory()
                setSearchPlaceholder(false)
                clearBtn.isVisible = false
            }
            searchText = searchField.text.toString()
            searchDebounce()
        }

        searchField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showHistory()
            }
        }

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            }
            false
        }

        findViewById<Button>(R.id.retry_search_button).setOnClickListener {
            setSearchPlaceholder(false)
            setNoInternetPlaceholder(false)
            loadTracks()
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun performSearch() {
        setSearchPlaceholder(false)
        setNoInternetPlaceholder(false)
        if (searchText.isEmpty()) return
        lastSearchQuery = searchText
        loadTracks()
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun setupRecycler() {
        searchPlaceholderLayout = findViewById(R.id.search_placeholder_layout)
        noInternetPlaceholderLayout = findViewById(R.id.no_internet_placeholder_layout)
        recycler = findViewById(R.id.search_recycler)
        currentHistory = history.readHistory(historyPrefs) ?: arrayListOf()
        adapter = SearchTrackAdapter(emptyList()) {
            if (clickDebounce()) {
                currentHistory.add(it)
                history.writeHistory(historyPrefs, currentHistory)
                val intent = Intent(this, AudioPlayerActivity::class.java)
                intent.putExtra("Track", it)
                startActivity(intent)
            }
        }
        recycler.adapter = adapter
        findViewById<Button>(R.id.clear_history_btn).setOnClickListener {
            history.writeHistory(historyPrefs, arrayListOf())
            currentHistory = arrayListOf()
            hideHistory()
        }

    }

    private fun setRecyclerHeight(isHistory: Boolean) {
        if (isHistory) {
            var heightInDp = 180
            val currentHistorySize = currentHistory.size
            if (currentHistorySize > 0 && currentHistorySize < 3) {
                heightInDp = 60 * currentHistorySize
            }
            val density = resources.displayMetrics.density
            val heightInPx = (heightInDp * density).toInt()

            recycler.layoutParams.height = heightInPx
            recycler.requestLayout()
        } else {
            recycler.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    private fun showHistory() {
        val history = history.readHistory(historyPrefs)
        if (history != null && history.isNotEmpty()) {
            adapter.updateData(history)
            historyPrefs.registerOnSharedPreferenceChangeListener(prefsChangeListener)
            setRecyclerHeight(true)
            findViewById<TextView>(R.id.you_searched_for_text).isVisible = true
            findViewById<Button>(R.id.clear_history_btn).isVisible = true
        } else {
            adapter.updateData(emptyList())
        }
    }

    private fun hideHistory() {
        historyPrefs.unregisterOnSharedPreferenceChangeListener(prefsChangeListener)
        adapter.updateData(emptyList())
        findViewById<TextView>(R.id.you_searched_for_text).isVisible = false
        findViewById<Button>(R.id.clear_history_btn).isVisible = false
        setRecyclerHeight(false)
    }

    private fun loadTracks() {
        searchProgressBar.isVisible = true
        val tracksInteractor = Creator.provideTracksInteractor()
        tracksInteractor.searchForTracks(
            expression = lastSearchQuery,
            object : TracksInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>?) {
                    searchProgressBar.isVisible = false

                    if (foundTracks == null) {
                        searchProgressBar.isVisible = false
                        setNoInternetPlaceholder(true)
                        adapter.updateData(emptyList())
                        return
                    }

                    if (foundTracks.isNotEmpty()) {
                        adapter.updateData(foundTracks)
                    } else {
                        setSearchPlaceholder(true)
                        adapter.updateData(emptyList())
                    }
                }

            }
        )
    }

    private fun setSearchPlaceholder(isVisible: Boolean) {
        searchPlaceholderLayout.isVisible = isVisible
        recycler.isVisible = !isVisible
    }

    private fun setNoInternetPlaceholder(isVisible: Boolean) {
        noInternetPlaceholderLayout.isVisible = isVisible
        recycler.isVisible = !isVisible
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        findViewById<EditText>(R.id.search_et)
            .setText(savedInstanceState.getString(TEXT_KEY, DEFAULT_TEXT))
    }

    companion object {
        private const val TEXT_KEY = "TEXT"
        private const val DEFAULT_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}