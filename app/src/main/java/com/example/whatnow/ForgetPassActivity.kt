package com.example.whatnow

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.whatnow.databinding.ActivityForgetPassBinding
import com.google.firebase.auth.FirebaseAuth

class ForgetPassActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityForgetPassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgetPassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.resetBtn.setOnClickListener {
            val email = binding.emailEt.text?.toString()?.trim()

            if (email.isNullOrEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.isVisible = true

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    binding.progressBar.isVisible = false
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Check your email for reset instructions",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            task.exception?.localizedMessage ?: "Error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
