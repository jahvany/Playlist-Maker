package com.practicum.playlistmaker.media.ui.fragments


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentEditPlaylistBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.ui.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.getValue

class FragmentEditPlaylist : Fragment() {

    val viewModel: EditPlaylistViewModel by viewModel()

    private lateinit var binding: FragmentEditPlaylistBinding

    private lateinit var inputName: EditText

    private lateinit var inputDescription: EditText

    private lateinit var addPhoto: ImageButton

    private var coverFileName: String? = null

    private lateinit var save: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlist = requireArguments().getParcelable<Playlist>(FragmentEditPlaylist.Companion.ARGS_PLAYLIST)

        addPhoto = binding.addPhoto

        inputName = binding.inputName

        inputDescription = binding.inputDescription

        save = binding.save

        playlist?.let {
            inputName.setText(it.name)
            inputDescription.setText(it.description)
            val filePath = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "myalbum"
            )
            val file = File(filePath, it.cover)

            Glide.with(addPhoto)
                .load(file)
                .placeholder(R.drawable.placeholder_playlist_holder)
                .error(R.drawable.placeholder_playlist_holder)
                .into(addPhoto)

        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.addPhoto.setImageURI(uri)
                    val filePath = File(
                        requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "myalbum"
                    )
                    if (!filePath.exists()) {
                        filePath.mkdirs()
                    }
                    coverFileName = "${System.currentTimeMillis()}.jpg"
                    val file = File(filePath, coverFileName)
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val outputStream = FileOutputStream(file)
                    try {
                        BitmapFactory
                            .decodeStream(inputStream)
                            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
                    } finally {
                        inputStream?.close()
                        outputStream.close()
                    }
                }
            }

        addPhoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        save.setOnClickListener {
            val newPlaylist = playlist?.copy(
                name = inputName.text.toString(),
                description = inputDescription.text.toString(),
                cover = coverFileName ?: playlist.cover
            )
            newPlaylist?.let {
                viewModel.updatePlaylist(it)
            }

            findNavController().popBackStack()
        }

    }
    companion object {
        private const val ARGS_PLAYLIST = "playlist"

        fun createArgs(playlist: Playlist?): Bundle =
            bundleOf(ARGS_PLAYLIST to playlist)
    }
}
