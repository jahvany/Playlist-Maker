package com.practicum.playlistmaker.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.api.TracksInteractor

class SearchActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "History"
        const val KEY_TRACK = "SearchHistory"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val titleSearch = findViewById<MaterialToolbar>(R.id.titleSearch)
        titleSearch.setNavigationOnClickListener { finish() }

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            trackAdapter.updateTracks(emptyList())
            inputEditText.setText("")
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
        }
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacks(searchRunnable)
                showTracks(inputEditText.text.toString())
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
                inputEditText.clearFocus()
                true
            } else {
                false
            }
        }

        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        tracksHistory = Gson()
            .fromJson(
                sharedPreferences
                    .getString(KEY_TRACK, null), Array<Track>::class.java
            )
            ?.toMutableList() ?: mutableListOf()

        textHistory = findViewById(R.id.textHistory)
        clearHistoryButton = findViewById(R.id.clearHistory)

        clearHistoryButton.setOnClickListener {
            tracksHistory.clear()
            trackAdapter.updateTracks(emptyList())
            textHistory.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && tracksHistory.isNotEmpty() && inputEditText.text.isEmpty()) {
                textHistory.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE
                trackAdapter.updateTracks(tracksHistory)
                erroreImage.visibility = View.GONE
                erroreText.visibility = View.GONE
                updateButton.visibility = View.GONE

            } else {
                textHistory.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
                trackAdapter.updateTracks(emptyList())
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                textForSave = s?.toString().orEmpty()
                searchDebounce(textForSave)
            }

            override fun afterTextChanged(s: Editable) {}
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
        inputEditText.setText(textForSave)

        val onTrackClickListener = OnTrackClickListener(
            this,
            tracksHistory,
            { clickDebounce() }
        )

        trackAdapter = TrackAdapter(emptyList(), onTrackClickListener)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = trackAdapter

        erroreImage = findViewById(R.id.erroreImage)
        erroreText = findViewById(R.id.erroreText)
        updateButton = findViewById(R.id.update)
        progressBar = findViewById(R.id.progressBar)

        updateButton.setOnClickListener {
            showTracks(inputEditText.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()

        if (textHistory.visibility == View.VISIBLE) {
            trackAdapter.updateTracks(tracksHistory)
        }
    }

    override fun onStop() {
        super.onStop()
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        sharedPreferences.edit()
            .putString(KEY_TRACK, Gson().toJson(tracksHistory))
            .apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    private lateinit var erroreImage: ImageView
    private lateinit var erroreText: TextView
    private lateinit var updateButton: Button
    private lateinit var textHistory: TextView
    private lateinit var clearHistoryButton: Button

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var tracksHistory: MutableList<Track>
    private lateinit var progressBar: ProgressBar

    private val tracksInteractor = Creator.provideTracksInteractor()

    var textForSave = ""

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("savedText", textForSave)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textForSave = savedInstanceState.getString("SAVED_TEXT", "")
    }

    private fun showTracks(text: String) {
        if (text.trim().isEmpty()) {
            if (tracksHistory.isNotEmpty()) {
                trackAdapter.updateTracks(tracksHistory)
                textHistory.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE
            } else {
                trackAdapter.updateTracks(emptyList())
            }
        } else {
            trackAdapter.updateTracks(emptyList())
            erroreImage.visibility = View.GONE
            erroreText.visibility = View.GONE
            updateButton.visibility = View.GONE
            textHistory.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            tracksInteractor.searchTracks(text, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>) {
                    trackAdapter.updateTracks(foundTracks)
                    updateButton.visibility = View.GONE
                    progressBar.visibility = View.GONE

                    if (foundTracks.isEmpty()) {
                        erroreImage.setImageResource(R.drawable.nothing)
                        erroreText.setText(R.string.searchNothing)
                        erroreImage.visibility = View.VISIBLE
                        erroreText.visibility = View.VISIBLE
                    } else {
                        erroreImage.visibility = View.GONE
                        erroreText.visibility = View.GONE
                    }
                }
                override fun onFailure(error: Throwable) {
                    trackAdapter.updateTracks(emptyList())
                    erroreImage.setImageResource(R.drawable.error)
                    erroreText.setText(R.string.searchError)
                    erroreImage.visibility = View.VISIBLE
                    erroreText.visibility = View.VISIBLE
                    updateButton.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            })
        }
    }

    var textForSearch = ""
    val searchRunnable = Runnable { showTracks(textForSearch) }
    private fun searchDebounce(text: String) {
        textForSearch = text
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}