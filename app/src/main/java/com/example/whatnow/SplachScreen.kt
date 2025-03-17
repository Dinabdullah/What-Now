package com.example.whatnow

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import android.widget.ImageView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_WhatNow)
        setContentView(R.layout.activity_splach_screen)

        // Load Animation
        val logo = findViewById<ImageView>(R.id.logo)
        val fadeInScale = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale)
        logo.startAnimation(fadeInScale)

        // Delay before navigating to MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, SignupActivity::class.java))
            finish() // Close SplashActivity
        }, 2000) // 2 seconds delay
    }
}
