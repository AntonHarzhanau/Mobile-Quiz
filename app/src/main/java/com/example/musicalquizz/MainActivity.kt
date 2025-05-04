package com.example.musicalquizz

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicalquizz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.botNavBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.btn_nav_home -> {
                    binding.tv.text = "Home"
                }
                R.id.btn_nav_search -> {
                    binding.tv.text = "Search"
                }
                R.id.btn_nav_settings -> {
                    binding.tv.text = "Settings"
                }
                R.id.btn_nav_playlist -> {
                    binding.tv.text = "Playlist"
                }
            }
            true
        }
    }
}