package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val titleSettings = findViewById<MaterialToolbar>(R.id.titleSettings)
        setSupportActionBar(titleSettings)
        supportActionBar?.title = getString(R.string.settings)
        titleSettings.setNavigationOnClickListener {finish()}
    }
}