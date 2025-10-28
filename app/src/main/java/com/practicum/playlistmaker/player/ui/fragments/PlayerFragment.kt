package com.practicum.playlistmaker.player.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.domain.model.PlayerState.Default
import com.practicum.playlistmaker.player.domain.model.PlayerState.Paused
import com.practicum.playlistmaker.player.domain.model.PlayerState.Playing
import com.practicum.playlistmaker.player.domain.model.PlayerState.Prepared
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class PlayerFragment : Fragment() {
    private lateinit var play: ImageButton
    private lateinit var playTime: TextView
    private lateinit var binding: FragmentPlayerBinding

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(requireArguments().getParcelable<Track>(ARGS_TRACK)?.previewUrl)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = requireArguments().getParcelable<Track>(ARGS_TRACK)

        binding.titlePlayer.setNavigationOnClickListener { findNavController().navigateUp() }

        val cover = binding.cover
        val radius = (8 * cover.context.resources.displayMetrics.density).roundToInt()

        play = binding.playButton

        playTime = binding.playTime

        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            update(it)
        }

        track?.let {
            Glide.with(cover)
                .load(it.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                .placeholder(R.drawable.bigplaceholder)
                .transform(RoundedCorners(radius))
                .into(cover)
            binding.trackName.text = it.trackName
            binding.artistName.text = it.artistName
            binding.timeNumber.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis)
            if (it.collectionName.isEmpty()) {
                binding.albumGroup.visibility = View.GONE
            } else {
                binding.albumGroup.visibility = View.VISIBLE
                binding.albumName.text = it.collectionName
            }
            if (it.releaseDate.isEmpty()) {
                binding.yearGroup.visibility = View.GONE
            } else {
                binding.yearGroup.visibility = View.VISIBLE
                binding.yearNumber.text = it.releaseDate.substring(0, 4)
            }
            binding.countryName.text = it.country
            binding.genreName.text = it.primaryGenreName
        }

        if (viewModel.stateLiveData.value == null ||
            viewModel.stateLiveData.value is PlayerState.Default) {
            viewModel.preparePlayer()
        }

        play.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    fun update(state: PlayerState) {
        playTime.text = state.timer
        when (state) {
            is Default -> play.animate().alpha(0.5f)
            is Prepared-> {
                play.animate().alpha(1f)
                play.setImageResource(R.drawable.play)
            }
            is Playing -> play.setImageResource(R.drawable.pause)
            is Paused -> play.setImageResource(R.drawable.play)
        }
    }

    override fun onPause() {
        super.onPause()
    }

    companion object {
        private const val ARGS_TRACK = "track"

        fun createArgs(track: Track): Bundle =
            bundleOf(ARGS_TRACK to track)
    }
}