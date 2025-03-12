package com.example.whatnow
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.whatnow.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // تفعيل View Binding
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // استرجاع بيانات المستخدم
        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        val username = sharedPref.getString("username", "User Name")
        val email = sharedPref.getString("email", "email@example.com")

        // عرض البيانات في الواجهة
        binding.tvUsername.text = username
        binding.tvEmail.text = email
    }
}
