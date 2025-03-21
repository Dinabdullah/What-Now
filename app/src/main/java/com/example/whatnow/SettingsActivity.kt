package com.example.whatnow

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.whatnow.databinding.ActivitySettingsBinding
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE)

        val userName = intent.getStringExtra("username")
        val name = sharedPreferences.getString("username", userName)
        val imageUri = intent.getStringExtra("image_uri")
        binding.tvSettingsUsername.text = name

        if (imageUri != null) {
            binding.profileIcon.setImageURI(Uri.parse(imageUri))
        }

        setupThemeSelector()
        setupCountrySelector()
        setupLanguageSelector()
        setupProfileSection()
    }

    override fun onResume() {
        super.onResume()
        val userName = intent.getStringExtra("username")
        val prefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val name = prefs.getString("username", userName)
        binding.tvSettingsUsername.text = name
    }

    private fun setupThemeSelector() {
        val themes = mapOf(
            "Light Mode" to AppCompatDelegate.MODE_NIGHT_NO,
            "Dark Mode" to AppCompatDelegate.MODE_NIGHT_YES,
            "Use Device Theme" to AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )

        val themeList = themes.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, themeList)
        binding.themeSpinner.adapter = adapter

        val savedTheme = sharedPreferences.getInt("app_theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        val selectedIndex = themeList.indexOfFirst { themes[it] == savedTheme }
        if (selectedIndex != -1) {
            binding.themeSpinner.setSelection(selectedIndex)
        }

        binding.saveThemeButton.setOnClickListener {
            val selectedTheme = binding.themeSpinner.selectedItem.toString()
            val selectedThemeMode = themes[selectedTheme] ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

            if (selectedThemeMode != savedTheme) {
                sharedPreferences.edit().putInt("app_theme", selectedThemeMode).apply()
                AppCompatDelegate.setDefaultNightMode(selectedThemeMode)
            }
        }
    }

    private fun setupCountrySelector() {
        val countries = mapOf("USA" to "us", "Germany" to "de", "Egypt" to "eg")
        val countryList = countries.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, countryList)
        binding.countrySpinner.adapter = adapter

        // Retrieve saved country setting
        val savedCountry = sharedPreferences.getString("news_country", "us") ?: "us"
        val selectedIndex = countryList.indexOfFirst { countries[it] == savedCountry }
        if (selectedIndex != -1) {
            binding.countrySpinner.setSelection(selectedIndex)
        }

        binding.saveCountryButton.setOnClickListener {
            val selectedCountry = binding.countrySpinner.selectedItem.toString()
            val selectedCountryCode = countries[selectedCountry] ?: "us"

            if (selectedCountryCode != savedCountry) {
                sharedPreferences.edit().putString("news_country", selectedCountryCode).apply()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }


    private fun setupLanguageSelector() {
        val languages = mapOf("English" to "en", "العربية" to "ar")
        val languageList = languages.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languageList)
        binding.languageSpinner.adapter = adapter

        val savedLanguage = sharedPreferences.getString("app_language", "en") ?: "en"
        val selectedIndex = languageList.indexOfFirst { languages[it] == savedLanguage }
        if (selectedIndex != -1) {
            binding.languageSpinner.setSelection(selectedIndex)
        }

        binding.saveLanguageButton.setOnClickListener {
            val selectedLanguage = binding.languageSpinner.selectedItem.toString()
            val selectedLanguageCode = languages[selectedLanguage] ?: "en"

            if (selectedLanguageCode != savedLanguage) {
                sharedPreferences.edit().putString("app_language", selectedLanguageCode).apply()
                setLocale(selectedLanguageCode)
            }
        }
    }

    private fun setupProfileSection() {
        val userPrefs = getSharedPreferences("user_data", MODE_PRIVATE)
        val username = userPrefs.getString("username", "User Name")

        binding.tvSettingsUsername.text = username
        binding.profileIcon.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        sharedPreferences.edit().putString("app_language", languageCode).apply()


        val savedTheme = sharedPreferences.getInt("app_theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedTheme)

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() //finish
    }
}
