package com.practicum.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaPlaylistsBinding
import com.practicum.playlistmaker.media.domain.models.PlaylistState
import com.practicum.playlistmaker.media.ui.view_model.PlaylistAdapter
import com.practicum.playlistmaker.media.ui.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FragmentPlaylists : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var nothingImage: ImageView
    private lateinit var nothingText: TextView

    private val viewModel: PlaylistViewModel by viewModel()

    private var _binding: FragmentMediaPlaylistsBinding? = null
    private val binding get() = _binding!!

    private lateinit var newPlaylist: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediaPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newPlaylist = binding.newPlaylist

        recyclerView = binding.recyclerView

        progressBar = binding.progressBar
        nothingImage = binding.nothingImage
        nothingText = binding.nothingText

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        playlistAdapter = PlaylistAdapter(emptyList())

        recyclerView.adapter = playlistAdapter

        viewModel.getPlaylists()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        newPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_fragmentNewPlaylist)
        }
    }

    private fun render(state: PlaylistState) {
        progressBar.isVisible = state is PlaylistState.Loading
        nothingImage.isVisible = state is PlaylistState.Empty
        nothingText.isVisible = state is PlaylistState.Empty
        recyclerView.isVisible = state is PlaylistState.Content

        when (state) {
            is PlaylistState.Loading, PlaylistState.Empty  -> {
                playlistAdapter.updatePlaylists(emptyList())
            }

            is PlaylistState.Content -> {
                playlistAdapter.updatePlaylists(state.playlists)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(number: Int) = FragmentPlaylists()
    }
}