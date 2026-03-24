package com.practicum.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.media.ui.compose.PlaylistsTab
import com.practicum.playlistmaker.media.ui.view_model.MediaPlaylistsViewModel
import com.practicum.playlistmaker.util.ui.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FragmentMediaPlaylists : Fragment() {

    private val viewModel: MediaPlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                PlaylistMakerTheme {
                    PlaylistsTab(viewModel, findNavController())
                }
            }
        }
    }
}