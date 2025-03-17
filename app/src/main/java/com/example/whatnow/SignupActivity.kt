package com.example.whatnow
import android.content.Intent
import android.os.Build

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.whatnow.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { it.remove() }
        }



        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth

        // Check if the user is already logged in
        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // Redirect to MainActivity and finish this activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding.signupBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()
            val conPass = binding.conpassEt.text.toString().trim()

            if (email.isBlank() || pass.isBlank() || conPass.isBlank()) {
                Toast.makeText(this, "Missing field(s)", Toast.LENGTH_SHORT).show()
            } else if (pass.length < 6) {
                Toast.makeText(this, "Password is too short", Toast.LENGTH_SHORT).show()
            } else if (pass != conPass) {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            } else {
                binding.progress.isVisible = true
                addUser(email, pass, binding)
            }
        }

        binding.alreadyUser.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun addUser(email: String, pass: String, binding: ActivitySignupBinding) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userName = binding.userName.text.toString().trim()
                    val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putBoolean("isLoggedIn", true)
                    editor.putString("username", userName)
                    editor.putString("email", email)
                    editor.apply()
                    VerifyEmail(binding)
                }
                else {
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    binding.progress.isVisible = false
                }
            }
    }

    private fun VerifyEmail(binding: ActivitySignupBinding) {
        val user = Firebase.auth.currentUser

        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                binding.progress.isVisible = false
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Verification email sent! Check your inbox.",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}
