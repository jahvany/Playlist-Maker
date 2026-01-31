package com.practicum.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaTracksBinding
import com.practicum.playlistmaker.media.domain.models.FavoriteState
import com.practicum.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.ui.fragments.PlayerFragment
import com.practicum.playlistmaker.search.ui.view_model.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FragmentFavoriteTracks : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var nothingImage: ImageView
    private lateinit var nothingText: TextView

    private val viewModel: FavoriteTracksViewModel by viewModel()

    private var _binding: FragmentMediaTracksBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediaTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = binding.progressBar
        nothingImage = binding.nothingImage
        nothingText = binding.nothingText
        recyclerView = binding.recyclerView

        trackAdapter = TrackAdapter(emptyList()) { track ->
            if (viewModel.clickDebounce()) {
                findNavController().navigate(
                    R.id.action_mediaFragment_to_playerFragment,
                    PlayerFragment.createArgs(track)
                )
            }
        }

        recyclerView.adapter = trackAdapter

        viewModel.loadFavoriteTracks()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: FavoriteState) {
        progressBar.isVisible = state is FavoriteState.Loading
        nothingImage.isVisible = state is FavoriteState.Empty
        nothingText.isVisible = state is FavoriteState.Empty
        recyclerView.isVisible = state is FavoriteState.Content

        when (state) {
            is FavoriteState.Loading, FavoriteState.Empty  -> {
                trackAdapter.updateTracks(emptyList())
            }

            is FavoriteState.Content -> {
                trackAdapter.updateTracks(state.tracks)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(number: Int) = FragmentFavoriteTracks()

    }
}