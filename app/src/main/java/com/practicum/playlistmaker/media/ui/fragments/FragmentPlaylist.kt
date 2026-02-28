package com.practicum.playlistmaker.media.ui.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.domain.models.PlaylistState
import com.practicum.playlistmaker.media.ui.view_model.PlaylistViewModel
import com.practicum.playlistmaker.media.ui.view_model.TrackPlaylistAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.sql.DataSource
import kotlin.getValue
import kotlin.math.roundToInt

class FragmentPlaylist: Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var progressBar: ProgressBar
    private lateinit var trackAdapter: TrackPlaylistAdapter
    private lateinit var cover: ImageView

    private lateinit var name: TextView

    private lateinit var year: TextView
    private lateinit var time: TextView
    private lateinit var numbers: TextView

    private lateinit var share: ImageButton

    private lateinit var settings: ImageButton

    private lateinit var sharePlaylist: Button

    private lateinit var editPlaylist: Button

    private lateinit var deletePlaylist: Button
    private lateinit var bottomSheetContainer: LinearLayout

    private lateinit var bottomSheetSetting: LinearLayout

    private lateinit var playlistName: TextView
    private lateinit var tracksNumber: TextView
    private lateinit var coverMini : ImageView

    private val viewModel: PlaylistViewModel by viewModel {
        parametersOf(
            requireArguments().getInt(ARGS_PLAYLIST_ID))
    }


    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.observePlaylist()

        recyclerView = binding.recyclerView

        cover = binding.cover
        name = binding.name
        year = binding.year
        time = binding.time
        numbers = binding.numbers
        progressBar = binding.progressBar
        bottomSheetContainer = binding.bottomsheet
        share = binding.share
        settings = binding.settings
        sharePlaylist = binding.sharePlaylist
        editPlaylist = binding.editPlaylist
        deletePlaylist = binding.deletePlaylist
        bottomSheetSetting = binding.bottomsheet2
        playlistName = binding.playlistName
        tracksNumber = binding.tracksNumber
        coverMini = binding.coverPlaylist

        binding.titlePlayer.setNavigationOnClickListener {
            findNavController().navigateUp()
        }


        trackAdapter = TrackPlaylistAdapter(emptyList()) { track ->
            val dialog = MaterialAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.delete))
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    viewModel.deleteTrackFromPlaylist(requireArguments().getInt(ARGS_PLAYLIST_ID),track.trackId)
                }
                .create()
            dialog.setOnShowListener {
                dialog.window?.setDimAmount(0.5f)
            }
            dialog.show()
        }

        recyclerView.adapter = trackAdapter

        viewModel.loadTracks()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        val bottomSheetSettingBehavior = BottomSheetBehavior.from(bottomSheetSetting).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetSettingBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        viewModel.playlistState.observe(viewLifecycleOwner) { playlist ->
            playlist?.let { updateUI(it) }
        }

        share.setOnClickListener {
            if (viewModel.currentPlaylist?.listOfTracks.isNullOrEmpty()) {
                val message = getString(R.string.zeroSharing)
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                viewModel.sharePlaylist()
            }
        }

        settings.setOnClickListener {
            bottomSheetSettingBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        sharePlaylist.setOnClickListener {
            if (viewModel.currentPlaylist?.listOfTracks.isNullOrEmpty()) {
                val message = getString(R.string.zeroSharing)
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                viewModel.sharePlaylist()
            }
        }

        editPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentPlaylist_to_fragmentNewPlaylist,
                FragmentNewPlaylist.createArgs(viewModel.currentPlaylist))
        }
        deletePlaylist.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.delete_message, name.text))
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    viewModel.deletePlaylist()
                    findNavController().navigateUp()
                }
                .create()
            dialog.setOnShowListener {
                dialog.window?.setDimAmount(0.5f)
            }
            dialog.show()
        }
    }

    private fun render(state: PlaylistState) {
        progressBar.isVisible = state is PlaylistState.Loading
        recyclerView.isVisible = state is PlaylistState.Content

        when (state) {
            is PlaylistState.Loading, PlaylistState.Empty  -> {
                trackAdapter.updateTracks(emptyList())
                time.text = requireContext().resources.getQuantityString(
                    R.plurals.minute_count,
                    0,
                    0
                )
                numbers.text = requireContext().resources.getQuantityString(
                    R.plurals.track_count,
                    0,
                    0
                )
                tracksNumber.text = requireContext().resources.getQuantityString(
                    R.plurals.track_count,
                    0,
                    0
                )
            }

            is PlaylistState.Content -> {
                trackAdapter.updateTracks(state.tracks)
                val timeSum = SimpleDateFormat("mm", Locale.getDefault()).format(state.tracks.sumOf { it.trackTimeMillis })
                time.text = requireContext().resources.getQuantityString(
                    R.plurals.minute_count,
                    timeSum.toInt(),
                    timeSum.toInt())
                numbers.text = requireContext().resources.getQuantityString(
                    R.plurals.track_count,
                    state.tracks.size,
                    state.tracks.size
                )
                tracksNumber.text = requireContext().resources.getQuantityString(
                    R.plurals.track_count,
                    state.tracks.size,
                    state.tracks.size
                )
            }
        }
    }

    private fun updateUI(playlist: Playlist) {
        name.text = playlist.name
        year.text = playlist.description
        playlistName.text = playlist.name
        tracksNumber.text = requireContext().resources.getQuantityString(
            R.plurals.track_count,
            playlist.numbersOfTracks,
            playlist.numbersOfTracks
        )

        val filePath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        val file = File(filePath, playlist.cover)

        Glide.with(cover)
            .load(file)
            .placeholder(R.drawable.placeholder_playlist_holder)
            .error(R.drawable.placeholder_playlist_holder)
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    cover.foreground = null
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    cover.foreground =
                        ContextCompat.getDrawable(requireContext(), R.drawable.white_frame)
                    return false
                }
            })
            .into(cover)

        val radius = (2 * coverMini.context.resources.displayMetrics.density).roundToInt()
        Glide.with(coverMini)
            .load(file)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .transform(RoundedCorners(radius))
            .into(coverMini)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            ?.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            ?.visibility = View.VISIBLE
    }

    companion object {
        private const val ARGS_PLAYLIST_ID = "playlistId"

        fun createArgs(playlist: Playlist): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlist.id)
    }
}