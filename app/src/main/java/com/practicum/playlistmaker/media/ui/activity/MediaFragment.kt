package com.practicum.playlistmaker.media.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaBinding
import com.practicum.playlistmaker.media.ui.view_model.MediaViewModel
import com.practicum.playlistmaker.media.ui.view_model.MediaViewPagerAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MediaFragment: Fragment() {

    val viewModel: MediaViewModel by viewModel()

    private lateinit var binding: FragmentMediaBinding

    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.titleMedia.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.viewPager.adapter = MediaViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)

        binding.tabLayout.setSelectedTabIndicator(R.drawable.custom_tab_indicator)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.mediaTabTracks)
                else -> getString(R.string.mediaTabPlaylist)
            }
        }
            tabMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
    }
}