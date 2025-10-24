package com.practicum.playlistmaker.search.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.activity.PlayerFragment
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.ui.view_model.TrackAdapter
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModel()

    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var erroreImage: ImageView
    private lateinit var erroreText: TextView
    private lateinit var updateButton: Button
    private lateinit var textHistory: TextView
    private lateinit var clearHistoryButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        inputEditText = binding.inputEditText
        clearButton = binding.clearIcon
        progressBar = binding.progressBar
        erroreImage = binding.erroreImage
        erroreText = binding.erroreText
        updateButton = binding.update
        textHistory = binding.textHistory
        clearHistoryButton = binding.clearHistory

        binding.titleSearch.setNavigationOnClickListener { findNavController().navigateUp() }

        trackAdapter = TrackAdapter(emptyList()) { track ->
            if (viewModel.clickDebounce()) {
                viewModel.saveTrack(track)
                findNavController().navigate(R.id.action_searchFragment_to_playerFragment,
                    PlayerFragment.createArgs(track))
            }
        }
        binding.recyclerView.adapter = trackAdapter

        viewModel.state.observe(viewLifecycleOwner) { render(it) }

        clearButton.setOnClickListener {
            inputEditText.text.clear()
        }

        inputEditText.doOnTextChanged { text, _, _, _ ->
            clearButton.isVisible = !text.isNullOrEmpty()
            viewModel.textForSave = text?.toString().orEmpty()
            viewModel.searchDebounce(viewModel.textForSave)
        }

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
                if(inputEditText.hasFocus()) {
                trackAdapter.updateTracks(state.tracks)
                textHistory.isVisible = state.tracks.isNotEmpty()
                clearHistoryButton.isVisible = state.tracks.isNotEmpty()
                }
                inputEditText.setOnFocusChangeListener { _, hasFocus ->
                    trackAdapter.updateTracks(state.tracks)
                    textHistory.isVisible = state.tracks.isNotEmpty()
                    clearHistoryButton.isVisible = state.tracks.isNotEmpty()
                }
            }
        }
    }
}