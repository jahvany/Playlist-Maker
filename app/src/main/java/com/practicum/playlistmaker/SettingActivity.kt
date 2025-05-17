package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backFromSettings = findViewById<ImageView>(R.id.backFromSettings)
        backFromSettings.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}