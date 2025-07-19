package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "History"
        const val KEY_TRACK = "SearchHistory"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val titleSearch = findViewById<MaterialToolbar>(R.id.titleSearch)
        titleSearch.setNavigationOnClickListener { finish() }

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            trackAdapter.updateTracks(emptyList())
            inputEditText.setText("")
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
        }
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                showTracks(inputEditText.text.toString())
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
                inputEditText.clearFocus()
                true
            } else {
                false
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                textForSave = s?.toString().orEmpty()
            }

            override fun afterTextChanged(s: Editable) {}
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
        inputEditText.setText(textForSave)

        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        tracksHistory = Gson()
            .fromJson(sharedPreferences
                .getString(KEY_TRACK, null), Array<Track>::class.java)
            ?.toMutableList() ?: mutableListOf()

        val onTrackClickListener = OnTrackClickListener(tracksHistory)

        val textHistory = findViewById<TextView>(R.id.textHistory)

        val clearHistoryButton = findViewById<Button>(R.id.clearHistory)
        clearHistoryButton.setOnClickListener {
            tracksHistory.clear()
            trackAdapter.updateTracks(emptyList())
            textHistory.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && !tracksHistory.isEmpty() && inputEditText.text.isEmpty()) {
                textHistory.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE
                trackAdapter.updateTracks(tracksHistory)
                erroreImage.visibility = View.GONE
                erroreText.visibility = View.GONE
                updateButton.visibility = View.GONE

            } else {
                textHistory.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
                trackAdapter.updateTracks(emptyList())
            }

        }

        trackAdapter = TrackAdapter(emptyList(), onTrackClickListener)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = trackAdapter

        erroreImage = findViewById(R.id.erroreImage)
        erroreText = findViewById(R.id.erroreText)
        updateButton = findViewById(R.id.update)

        updateButton.setOnClickListener {
            showTracks(inputEditText.text.toString())
        }
    }

    override fun onStop() {
        super.onStop()
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        sharedPreferences.edit()
            .putString(KEY_TRACK, Gson().toJson(tracksHistory))
            .apply()
    }

    private lateinit var erroreImage: ImageView
    private lateinit var erroreText: TextView
    private lateinit var updateButton: Button

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var tracksHistory: MutableList<Track>
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesApiService = retrofit.create(iTunesApi::class.java)

    var textForSave = ""

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("savedText", textForSave)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textForSave = savedInstanceState.getString("SAVED_TEXT", "")
    }

    private fun showTracks(text: String) {
        iTunesApiService.search(text)
            .enqueue(object : Callback<iTunesResponse> {
                override fun onResponse(
                    call: Call<iTunesResponse>,
                    response: Response<iTunesResponse>
                ) {
                    val tracks = response.body()?.results ?: emptyList()
                    trackAdapter.updateTracks(tracks)
                    updateButton.visibility = View.GONE
                    if (tracks.isEmpty()) {
                        erroreImage.setImageResource(R.drawable.nothing)
                        erroreText.setText(R.string.searchNothing)
                        erroreImage.visibility = View.VISIBLE
                        erroreText.visibility = View.VISIBLE

                    } else {
                        erroreImage.visibility = View.GONE
                        erroreText.visibility = View.GONE

                    }
                }

                override fun onFailure(call: Call<iTunesResponse>, t: Throwable) {
                    trackAdapter.updateTracks(emptyList())
                    erroreImage.setImageResource(R.drawable.error)
                    erroreText.setText(R.string.searchError)
                    erroreImage.visibility = View.VISIBLE
                    erroreText.visibility = View.VISIBLE
                    updateButton.visibility = View.VISIBLE
                }
            }
            )
    }
}