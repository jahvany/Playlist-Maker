package com.practicum.playlistmaker.media.ui.view_model

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentMediaPlaylistsBinding

class FragmentPlaylists : Fragment() {
    companion object {
        private const val NUMBER = "number"

        fun newInstance(number: Int) = FragmentPlaylists().apply {
            arguments = Bundle().apply {
                putInt(NUMBER, number)
            }
        }
    }

    private lateinit var binding: FragmentMediaPlaylistsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }
}