package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView


class SearchActivity : AppCompatActivity() {

    private var searchText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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
            } else {
                clearBtn.isVisible = false
            }
        }
    }

    private fun setupRecycler() {
        val recycler = findViewById<RecyclerView>(R.id.search_recycler)
        val adapter = SearchTrackAdapter(getMockData())
        recycler.adapter = adapter
    }

    private fun getMockData(): List<Track> {
        return listOf(
            Track(
                trackName = "Smells Like Teen Spirit",
                artistName = "Nirvana",
                trackTime = "5:01",
                artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg",
            ),
            Track(
                trackName = "Billie Jean",
                artistName = "Michael Jackson",
                trackTime = "4:35",
                artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg",
            ),
            Track(
                trackName = "Stayin' Alive",
                artistName = "Bee Gees",
                trackTime = "4:10",
                artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg",
            ),
            Track(
                trackName = "Whole Lotta Love",
                artistName = "Led Zeppelin",
                trackTime = "5:33",
                artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg",
            ),
            Track(
                trackName = "Sweet Child O'Mine\n",
                artistName = "Guns N' Roses",
                trackTime = "5:03",
                artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg",
            ),
        )
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