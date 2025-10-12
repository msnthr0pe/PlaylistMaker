package com.practicum.playlistmaker

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Response


class SearchActivity : AppCompatActivity() {

    private var searchText = ""
    private var lastSearchQuery = ""
    private lateinit var adapter: SearchTrackAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var searchPlaceholderLayout: LinearLayout
    private lateinit var noInternetPlaceholderLayout: LinearLayout
    private val history by lazy { SearchHistory() }
    private val historyPrefs by lazy { getSharedPreferences(HISTORY_PREFS_NAME, MODE_PRIVATE) }
    private var currentHistory: ArrayList<Track> = arrayListOf()
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
                searchText = searchField.text.toString()
                hideHistory()
            } else {
                showHistory()
                clearBtn.isVisible = false
            }
        }

        searchField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showHistory()
            }
        }

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setSearchPlaceholder(false)
                setNoInternetPlaceholder(false)
                lastSearchQuery = searchText
                loadTracks()
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

    private fun setupRecycler() {
        searchPlaceholderLayout = findViewById(R.id.search_placeholder_layout)
        noInternetPlaceholderLayout = findViewById(R.id.no_internet_placeholder_layout)
        recycler = findViewById(R.id.search_recycler)
        currentHistory = history.readHistory(historyPrefs) ?: arrayListOf()
        adapter = SearchTrackAdapter(emptyList()) {
            currentHistory.add(it)
            history.writeHistory(historyPrefs, currentHistory)
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
            Log.d("History", history.toString())
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
        SearchRetrofit.searchMusicApi.search(text = lastSearchQuery).enqueue(object : retrofit2.Callback<TrackSearchResponse> {
            override fun onResponse(
                call: Call<TrackSearchResponse?>,
                response: Response<TrackSearchResponse?>,
            ) {
                if (response.isSuccessful) {
                    val tracks: List<Track> = response.body()?.results?: emptyList()
                    if (tracks.isNotEmpty()) {
                        adapter.updateData(tracks)
                    } else {
                        setSearchPlaceholder(true)
                        adapter.updateData(emptyList())
                    }
                }
            }

            override fun onFailure(
                call: Call<TrackSearchResponse?>,
                t: Throwable,
            ) {
                setNoInternetPlaceholder(true)
                adapter.updateData(emptyList())
            }

        })
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
        const val TEXT_KEY = "TEXT"
        const val DEFAULT_TEXT = ""
    }
}