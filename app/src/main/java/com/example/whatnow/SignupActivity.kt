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
import com.example.whatnow.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var progress: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth


        val emailET: EditText = binding.emailEt
        val passET: EditText = binding.passEt
        val conPassET: EditText = binding.conpassEt
        val aleardyUser: TextView = binding.alreadyUser
        val btn: Button = binding.signupBtn
        progress = binding.progress


        btn.setOnClickListener {
            val email = emailET.text.toString()
            val pass = passET.text.toString()
            val conPass = conPassET.text.toString()
            if (email.isBlank() || pass.isBlank() || conPass.isBlank())
                Toast.makeText(this, "Missing filed/s", Toast.LENGTH_SHORT).show()
            else if (pass.length < 6)
                Toast.makeText(this, "Password is too short", Toast.LENGTH_SHORT).show()
            else if (pass != conPass)
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            else {
                progress.isVisible = true
                addUser(email, pass)
            }
        }
        aleardyUser.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun addUser(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    VerifyEmail()
                } else
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                progress.isVisible = false

            }
    }

    private fun VerifyEmail() {
        val user = Firebase.auth.currentUser

        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show()
                    progress.isVisible = false
                }
            }


    }

}