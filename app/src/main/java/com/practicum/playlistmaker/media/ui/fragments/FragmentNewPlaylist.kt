package com.practicum.playlistmaker.media.ui.fragments


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.ui.view_model.NewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.getValue

class FragmentNewPlaylist : Fragment() {

    val viewModel: NewPlaylistViewModel by viewModel()

    private lateinit var binding: FragmentNewPlaylistBinding

    private lateinit var inputName: EditText

    private lateinit var inputDescription: EditText

    private lateinit var addPhoto: ImageButton

    lateinit var confirmDialog: MaterialAlertDialogBuilder
    private lateinit var title: MaterialToolbar

    private var coverFileName: String = ""

    private lateinit var create: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlist = arguments?.getParcelable<Playlist>(FragmentNewPlaylist.Companion.ARGS_PLAYLIST)

        addPhoto = binding.addPhoto

        inputName = binding.inputName

        inputDescription = binding.inputDescription

        create = binding.create

        title = binding.titleMedia

        title.setNavigationOnClickListener {
            handleExit()
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

        if (playlist != null) {

            inputName.setText(playlist.name)
            inputDescription.setText(playlist.description)
            create.text = getString(R.string.save)
            title.title = getString(R.string.edit)


            val filePath = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "myalbum"
            )
            val file = File(filePath, playlist.cover)

            Glide.with(addPhoto)
                .load(file)
                .placeholder(R.drawable.placeholder_playlist_holder)
                .error(R.drawable.placeholder_playlist_holder)
                .into(addPhoto)

            create.setOnClickListener {
                val updatedPlaylist = playlist.copy(
                    name = inputName.text.toString(),
                    description = inputDescription.text.toString(),
                    cover = coverFileName ?: playlist.cover
                )

                viewModel.updatePlaylist(updatedPlaylist)
                findNavController().popBackStack()
            }

        } else {
            create.isEnabled = false

            inputName.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {
                    create.isEnabled = !s.isNullOrBlank()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            create.text = getString(R.string.create)
            title.title = getString(R.string.playlistForm)
            create.setOnClickListener {
                val name = inputName.text.toString()
                val message = getString(R.string.playlist_message, name)

                val newPlaylist = Playlist(
                    name = name,
                    description = inputDescription.text.toString(),
                    cover = coverFileName,
                    numbersOfTracks = 0,
                    listOfTracks = ""
                )

                viewModel.createPlaylist(newPlaylist)
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            confirmDialog = MaterialAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.dialogTitle))
                .setMessage(getString(R.string.dialogMessage))
                .setNeutralButton(getString(R.string.dialogCancel)) { dialog, which ->
                }.setNegativeButton(getString(R.string.dialogFinish)) { dialog, which ->
                    findNavController().popBackStack()
                }
            requireActivity()
                .onBackPressedDispatcher
                .addCallback(viewLifecycleOwner,
                    object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            handleExit()
                        }
                    })
        }
    }
    private fun handleExit() {
        if (!inputName.text.isNullOrBlank()
            || !inputDescription.text.isNullOrBlank()
            || coverFileName.isNotEmpty()
        ) {
            confirmDialog.show()
        } else {
            findNavController().popBackStack()
        }
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
        private const val ARGS_PLAYLIST = "playlist"

        fun createArgs(playlist: Playlist?): Bundle =
            bundleOf(ARGS_PLAYLIST to playlist)
    }
}

