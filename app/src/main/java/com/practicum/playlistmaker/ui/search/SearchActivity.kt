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
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.data.search.history.impl.HISTORY_PREFS_KEY
import com.practicum.playlistmaker.data.search.history.impl.HISTORY_PREFS_NAME
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.ui.search.viewmodel.SearchViewModel
import com.practicum.playlistmaker.ui.player.AudioPlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var searchText = ""
    private var lastSearchQuery = ""
    private lateinit var adapter: SearchTrackAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var searchPlaceholderLayout: LinearLayout
    private lateinit var noInternetPlaceholderLayout: LinearLayout
    private lateinit var searchProgressBar: ProgressBar
    private val historyPrefs by lazy { getSharedPreferences(HISTORY_PREFS_NAME, MODE_PRIVATE) }
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch() }
    private var isClickAllowed = true
    private var tracksSize = 0
    private val viewModel: SearchViewModel by viewModel()

    private val prefsChangeListener by lazy {
        SharedPreferences.OnSharedPreferenceChangeListener{ prefs, key ->
            if (key == HISTORY_PREFS_KEY) {
                viewModel.updateDisplayedTracks()
                setRecyclerHeight(true)
            }
        }
    }

    private fun setViewModelObservers() {
        with (viewModel) {
            observeSearchState.observe(this@SearchActivity) {
                adapter.updateData(it.displayedTracks.reversed())
                tracksSize = it.displayedTracks.size

                setSearchPlaceholder(it.placeholdersState.searchPlaceholderVisible)
                setNoInternetPlaceholder(it.placeholdersState.noInternetPlaceholderVisible)
                setRecyclerHeight(it.isHistoryEnabled)
                binding.apply {
                    if (tracksSize == 0) {
                        youSearchedForText.isVisible = false
                        clearHistoryBtn.isVisible = false
                    } else {
                        youSearchedForText.isVisible = it.isHistoryEnabled
                        clearHistoryBtn.isVisible = it.isHistoryEnabled
                    }
                }
                if (it.displayedTracks.isNotEmpty() ||
                    it.placeholdersState.searchPlaceholderVisible ||
                    it.placeholdersState.noInternetPlaceholderVisible ||
                    it.isHistoryEnabled
                    ) {
                    searchProgressBar.isVisible = false
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setViewModelObservers()

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecycler()

        searchProgressBar = binding.searchProgressBar

        val searchField = binding.searchEt
        val clearBtn = binding.clearTextBtn

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

        binding.backSearchBtn.setOnClickListener {
            finish()
        }

        searchField.doOnTextChanged { _, _, _, _ ->
            if (searchField.text.isNotEmpty()) {
                clearBtn.isVisible = true
                hideHistory()
            } else {
                showHistory()
                viewModel.updatePlaceholdersState(search = false, noInternet = false)
                clearBtn.isVisible = false
            }
            searchText = searchField.text.toString()
            searchDebounce()
        }

        searchField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showHistory()
                viewModel.updatePlaceholdersState(search = false, noInternet = false)
            }
        }

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideHistory()
                handler.removeCallbacks(searchRunnable)
                handler.post(searchRunnable)
                true
            }
            false
        }

        binding.retrySearchButton.setOnClickListener {
            viewModel.updatePlaceholdersState(search = false, noInternet = false)
            loadTracks()
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun performSearch() {
        viewModel.updatePlaceholdersState(search = false, noInternet = false)
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
        searchPlaceholderLayout = binding.searchPlaceholderLayout
        noInternetPlaceholderLayout = binding.noInternetPlaceholderLayout
        recycler = binding.searchRecycler

        viewModel.updateCurrentHistory()
        adapter = SearchTrackAdapter(emptyList()) {
            if (clickDebounce()) {
                viewModel.addToHistory(it)
                viewModel.addHistory()
                val intent = Intent(this, AudioPlayerActivity::class.java)
                intent.putExtra("Track", it)
                startActivity(intent)
            }
        }
        recycler.adapter = adapter
        binding.clearHistoryBtn.setOnClickListener {
            viewModel.addHistory(arrayListOf())
            viewModel.updateCurrentHistory()
            hideHistory()
        }

    }

    private fun setRecyclerHeight(isHistory: Boolean) {
        if (isHistory) {
            var heightInDp = 180
            val currentHistorySize = tracksSize
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
        viewModel.showHistory()
        historyPrefs.registerOnSharedPreferenceChangeListener(prefsChangeListener)
        viewModel.updateHistoryEnablement(true)
    }

    private fun hideHistory() {
        historyPrefs.unregisterOnSharedPreferenceChangeListener(prefsChangeListener)

        viewModel.updateDisplayedTracks(arrayListOf())
        viewModel.updateHistoryEnablement(false)
    }

    private fun loadTracks() {
        searchProgressBar.isVisible = true
        viewModel.loadTracks(lastSearchQuery)
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

        binding.searchEt
            .setText(savedInstanceState.getString(TEXT_KEY, DEFAULT_TEXT))
    }

    companion object {
        private const val TEXT_KEY = "TEXT"
        private const val DEFAULT_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}