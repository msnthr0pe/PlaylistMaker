package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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
            adapter.updateData(emptyList())

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
            } else {
                clearBtn.isVisible = false
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
        adapter = SearchTrackAdapter(emptyList())
        recycler.adapter = adapter
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