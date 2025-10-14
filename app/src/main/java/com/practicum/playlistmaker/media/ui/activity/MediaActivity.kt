package com.practicum.playlistmaker.media.ui.activity

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaBinding
import com.practicum.playlistmaker.media.ui.view_model.MediaViewModel
import com.practicum.playlistmaker.media.ui.view_model.MediaViewPagerAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MediaActivity: AppCompatActivity() {

    val viewModel: MediaViewModel by viewModel()

    private lateinit var binding: ActivityMediaBinding

    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.titleMedia.setNavigationOnClickListener { finish() }

        binding.viewPager.adapter = MediaViewPagerAdapter(supportFragmentManager, lifecycle)

        binding.tabLayout.setSelectedTabIndicator(R.drawable.custom_tab_indicator)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.mediaTabTracks)
                1 -> getString(R.string.mediaTabPlaylist)
                else -> ""
            }
        }
        tabMediator.attach()
        updateTabsTextCaps()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) { updateTabsTextCaps() }
            override fun onTabUnselected(tab: TabLayout.Tab?) { updateTabsTextCaps() }
            override fun onTabReselected(tab: TabLayout.Tab?) { updateTabsTextCaps() }
        })
    }

    private fun updateTabsTextCaps() {
        for (i in 0 until binding.tabLayout.tabCount) {
            val tabView = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i) as ViewGroup
            val textView = tabView.getChildAt(1) as? TextView
            textView?.isAllCaps = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}