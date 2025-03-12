package com.example.whatnow

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
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

        setupThemeSwitch()
        setupCountrySelector()
        setupLanguageSelector()
        setupProfileSection()
    }

    private fun setupThemeSwitch() {
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        binding.themeSwitch.isChecked = isDarkMode

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
        }
    }

    private fun setupCountrySelector() {
        val countries = mapOf("USA" to "us", "Germany" to "de", "Egypt" to "eg")
        val countryList = countries.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, countryList)
        binding.countrySpinner.adapter = adapter

        val savedCountry = sharedPreferences.getString("news_country", "us") ?: "us"
        val selectedIndex = countryList.indexOfFirst { countries[it] == savedCountry }
        if (selectedIndex != -1) {
            binding.countrySpinner.setSelection(selectedIndex)
        }

        binding.saveCountryButton.setOnClickListener {
            val selectedCountry = binding.countrySpinner.selectedItem.toString()
            sharedPreferences.edit().putString("news_country", countries[selectedCountry]).apply()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupLanguageSelector() {
        val languages = mapOf("English" to "en", "العربية" to "ar")
        val languageList = languages.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languageList)
        binding.languageSpinner.adapter = adapter

        val savedLanguage = sharedPreferences.getString("app_language", "en")
        val selectedIndex = languageList.indexOfFirst { languages[it] == savedLanguage }
        if (selectedIndex != -1) {
            binding.languageSpinner.setSelection(selectedIndex)
        }

        binding.saveLanguageButton.setOnClickListener {
            val selectedLanguage = binding.languageSpinner.selectedItem.toString()
            setLocale(languages[selectedLanguage]!!)
        }
    }

    private fun setupProfileSection() {
        val userPrefs = getSharedPreferences("UserData", MODE_PRIVATE)
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
        recreate()
    }
}