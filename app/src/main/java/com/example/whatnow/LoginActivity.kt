package com.example.whatnow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.whatnow.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var progress: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        val emailET: EditText = binding.etEmail
        val passlET: EditText =binding.etPass
        val createAcc: TextView = binding.createAcc
        progress = binding.progress
        val btn: Button =binding.loginBtn
        val forgotpass: TextView =binding.forgetPass





        btn.setOnClickListener {
            val email = emailET.text.toString()
            val pass = passlET.text.toString()

            if (email.isBlank() || pass.isBlank())
                Toast.makeText(this, "Missing filed/s", Toast.LENGTH_SHORT).show()
            else {
                progress.isVisible = true
                login(email, pass)
            }
        }

        createAcc.setOnClickListener {
            startActivity(Intent(this, createAcc::class.java))
            finish()
        }

        forgotpass.setOnClickListener {
            progress.isVisible = true
            val email = emailET.text.toString()
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        Toast.makeText(this, "Check Email", Toast.LENGTH_SHORT).show()
                    progress.isVisible = false
                }
        }
    }

    private fun login(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser!!.isEmailVerified)
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this, "Check your email!!!!", Toast.LENGTH_SHORT).show()
                } else
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                progress.isVisible = false
            }
    }
}