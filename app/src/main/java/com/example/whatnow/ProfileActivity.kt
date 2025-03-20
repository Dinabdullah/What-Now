package com.example.whatnow

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.whatnow.databinding.ActivityProfileBinding
import com.example.whatnow.profile.FullScreenImageActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val PICK_IMAGE_REQUEST = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userName = intent.getStringExtra("username")
        val mail = intent.getStringExtra("email")

        val prefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val name = prefs.getString("user_name", userName)
        val email = prefs.getString("user_email", mail)
        val imageUri = prefs.getString("profile_image", null)

        binding.tvUserName.text = name
        binding.tvEmail.text = email

        if (imageUri != null) {
            binding.profileImage.setImageURI(Uri.parse(imageUri))
        }

        binding.tvUserName.setOnClickListener {
            showEditNameDialog()
        }

        binding.profileImage.setOnClickListener {
            showImageOptionsDialog()
        }
        binding.logoutBtn.setOnClickListener{
            val prefs = getSharedPreferences("user_data", Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showEditNameDialog() {
        val prefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val currentName = prefs.getString("user_name", "")

        val editText = EditText(this)
        editText.setText(currentName)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Name")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    binding.tvUserName.text = newName

                    val editor = prefs.edit()
                    editor.putString("user_name", newName)
                    editor.apply()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun showImageOptionsDialog() {
        val options = arrayOf("View Photo", "Edit Photo")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Profile Photo")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> viewFullScreenImage() // View Photo
                1 -> pickImageFromGallery() // Edit Photo
            }
        }
        builder.show()
    }

    private fun viewFullScreenImage() {
        val prefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val imageUri = prefs.getString("profile_image", null)

        if (imageUri != null) {
            val intent = Intent(this, FullScreenImageActivity::class.java)
            intent.putExtra("image_uri", imageUri)
            startActivity(intent)
        } else {
            Toast.makeText(this, "No image available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                binding.profileImage.setImageURI(selectedImageUri)

                // Save image URI in SharedPreferences
                val prefs = getSharedPreferences("user_data", Context.MODE_PRIVATE).edit()
                prefs.putString("profile_image", selectedImageUri.toString())
                prefs.apply()
            }
        }
    }
}
