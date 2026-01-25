package com.practicum.playlistmaker.player.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.player.domain.model.PlayerStatus
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(
            requireArguments()
                .getParcelable<Track>(ARGS_TRACK)
                ?.previewUrl
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = requireArguments().getParcelable<Track>(ARGS_TRACK)

        binding.titlePlayer.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupTrackInfo(track)

        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.likeButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        track?.let {
            viewModel.setTrack(it)
            viewModel.preparePlayer()
        }
    }

    private fun setupTrackInfo(track: Track?) {
        track ?: return

        val radius = (8 * resources.displayMetrics.density).roundToInt()

        Glide.with(binding.cover)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.bigplaceholder)
            .transform(RoundedCorners(radius))
            .into(binding.cover)

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.timeNumber.text =
            SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(track.trackTimeMillis)

        binding.albumGroup.isVisible = track.collectionName.isNotEmpty()
        binding.albumName.text = track.collectionName

        binding.yearGroup.isVisible = track.releaseDate.isNotEmpty()
        binding.yearNumber.text = track.releaseDate.take(4)

        binding.countryName.text = track.country
        binding.genreName.text = track.primaryGenreName
    }

    private fun render(state: PlayerState) {
        binding.playTime.text = state.timer

        when (state.status) {
            PlayerStatus.DEFAULT -> {
                binding.playButton.alpha = 0.5f
            }

            PlayerStatus.PREPARED -> {
                binding.playButton.alpha = 1f
                binding.playButton.setImageResource(R.drawable.play)
            }

            PlayerStatus.PLAYING -> {
                binding.playButton.setImageResource(R.drawable.pause)
            }

            PlayerStatus.PAUSED -> {
                binding.playButton.setImageResource(R.drawable.play)
            }
        }

        binding.likeButton.setImageResource(
            if (state.isFavorite) {
                R.drawable.likeyes
            } else {
                R.drawable.likeno
            }
        )
    }

    companion object {
        private const val ARGS_TRACK = "track"

        fun createArgs(track: Track): Bundle =
            bundleOf(ARGS_TRACK to track)
    }
}
