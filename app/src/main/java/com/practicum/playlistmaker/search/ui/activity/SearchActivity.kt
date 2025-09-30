package com.practicum.playlistmaker.search.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.activity.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.ui.view_model.TrackAdapter
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.util.Creator
import kotlin.getValue

class SearchActivity : AppCompatActivity() {

    private lateinit var trackAdapter: TrackAdapter

    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var erroreImage: ImageView
    private lateinit var erroreText: TextView
    private lateinit var updateButton: Button
    private lateinit var textHistory: TextView
    private lateinit var clearHistoryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val viewModel: SearchViewModel by viewModels {
            SearchViewModel.getFactory(
                Creator.provideTracksInteractor(),
                Creator.provideSearchHistoryInteractor(this)
            )
        }

        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearIcon)
        progressBar = findViewById(R.id.progressBar)
        erroreImage = findViewById(R.id.erroreImage)
        erroreText = findViewById(R.id.erroreText)
        updateButton = findViewById(R.id.update)
        textHistory = findViewById(R.id.textHistory)
        clearHistoryButton = findViewById(R.id.clearHistory)

        val titleSearch = findViewById<MaterialToolbar>(R.id.titleSearch)
        titleSearch.setNavigationOnClickListener { finish() }

        trackAdapter = TrackAdapter(emptyList()) { track ->
            if (viewModel.clickDebounce()) {
                viewModel.saveTrack(track)
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("track", track)
                startActivity(intent)
            }
        }
        findViewById<RecyclerView>(R.id.recyclerView).adapter = trackAdapter

        viewModel.state.observe(this) { render(it) }

        clearButton.setOnClickListener {
            inputEditText.text.clear()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                viewModel.textForSave = s?.toString().orEmpty()
                viewModel.searchDebounce(viewModel.textForSave)
            }

            override fun afterTextChanged(s: Editable) {}
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        updateButton.setOnClickListener {
            viewModel.search(inputEditText.text.toString())
        }

        inputEditText.setText(viewModel.textForSave)
    }

    private fun render(state: SearchState) {
        progressBar.isVisible = state is SearchState.Loading
        erroreImage.isVisible = state is SearchState.Error || state is SearchState.NothingFound
        erroreText.isVisible = state is SearchState.Error || state is SearchState.NothingFound
        updateButton.isVisible = state is SearchState.Error

        when (state) {
            is SearchState.Loading, is SearchState.Empty -> {
                textHistory.isVisible = false
                clearHistoryButton.isVisible = false
                trackAdapter.updateTracks(emptyList())
            }
            is SearchState.Content -> {
                trackAdapter.updateTracks(state.tracks)
            }
            is SearchState.NothingFound -> {
                erroreImage.setImageResource(R.drawable.nothing)
                erroreText.setText(R.string.searchNothing)
                trackAdapter.updateTracks(emptyList())
            }
            is SearchState.Error -> {
                erroreImage.setImageResource(R.drawable.error)
                erroreText.setText(R.string.searchError)
                trackAdapter.updateTracks(emptyList())
            }
            is SearchState.History -> {
                trackAdapter.updateTracks(state.tracks)
                textHistory.isVisible = state.tracks.isNotEmpty()
                clearHistoryButton.isVisible = state.tracks.isNotEmpty()
            }
        }
    }
}