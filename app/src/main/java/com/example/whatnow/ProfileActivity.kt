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

        val prefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val name = prefs.getString("username", "Unknown User")
        val email = prefs.getString("email", "No Email")
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

        binding.logoutBtn.setOnClickListener {
            logoutUser()
        }
    }

    private fun showEditNameDialog() {
        val prefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val currentName = prefs.getString("username", "")

        val editText = EditText(this)
        editText.setText(currentName)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Name")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    binding.tvUserName.text = newName

                    // ✅ تحديث الاسم في SharedPreferences
                    val editor = prefs.edit()
                    editor.putString("username", newName)
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

    @Deprecated("Use Activity Result API instead")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                binding.profileImage.setImageURI(selectedImageUri)

                // ✅ حفظ الصورة في SharedPreferences
                val prefs = getSharedPreferences("user_data", Context.MODE_PRIVATE).edit()
                prefs.putString("profile_image", selectedImageUri.toString())
                prefs.apply()
            }
        }
    }

    private fun logoutUser() {
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
