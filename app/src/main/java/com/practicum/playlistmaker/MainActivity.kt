package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val titleMain = findViewById<MaterialToolbar>(R.id.titleMain)
        setSupportActionBar(titleMain)

        val buttonSearch = findViewById<Button>(R.id.search)
        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
            }
        }
        buttonSearch.setOnClickListener(buttonClickListener)

        val buttonMedia = findViewById<Button>(R.id.media)
        buttonMedia.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        val buttonSettings = findViewById<Button>(R.id.settings)
        buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }
}