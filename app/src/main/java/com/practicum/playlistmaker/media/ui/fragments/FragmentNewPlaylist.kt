package com.practicum.playlistmaker.media.ui.fragments


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

        addPhoto = binding.addPhoto

        inputName = binding.inputName

        inputDescription = binding.inputDescription

        create = binding.create

        create.isEnabled = false

        inputName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                create.isEnabled = !s.isNullOrBlank()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        confirmDialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.dialogTitle))
            .setMessage(getString(R.string.dialogMessage))
            .setNeutralButton(getString(R.string.dialogCancel)) { dialog, which ->
            }.setNegativeButton(getString(R.string.dialogFinish)) { dialog, which ->
                findNavController().popBackStack()
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
                    BitmapFactory
                        .decodeStream(inputStream)
                        .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        addPhoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

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

            findNavController().popBackStack()

            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner,
                object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!inputName.text.isNullOrBlank() || !inputDescription.text.isNullOrBlank() || coverFileName.isNotEmpty())
                    confirmDialog.show()
                else findNavController().popBackStack()
            }
        })
    }
}
