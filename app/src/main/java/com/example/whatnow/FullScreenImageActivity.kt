package com.example.whatnow.profile

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.whatnow.databinding.ActivityFullScreenImageBinding

class FullScreenImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra("image_uri")

        if (imageUri != null) {
            binding.fullScreenImage.setImageURI(Uri.parse(imageUri))
        } else {
            finish()
        }

        binding.closeButton.setOnClickListener {
            finish()
        }
    }
}
