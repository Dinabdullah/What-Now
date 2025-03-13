package com.example.whatnow

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.whatnow.databinding.ActivityForgetPassBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

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

                    val message = if (task.isSuccessful) {
                        "If You SignedIn before, you'll receive reset Email"
                    } else {
                        when (task.exception) {
                            is FirebaseAuthInvalidUserException ->
                                "If this email is registered, you'll receive reset instructions"
                            else ->
                                task.exception?.message ?: "Failed to send reset email"
                        }
                    }

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
        }
    }
}