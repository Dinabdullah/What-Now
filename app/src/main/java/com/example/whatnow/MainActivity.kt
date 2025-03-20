package com.example.whatnow

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatnow.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var newsCallable: NewsCallable
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

                val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

                bottomNavigationView.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.nav_home -> {
                            if (this::class.java != MainActivity::class.java) {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                startActivity(intent)
                            }
                            true
                        }

                        R.id.nav_settings -> {
                            startActivity(Intent(this, SettingsActivity::class.java))
                            true
                        }

                        R.id.nav_profile -> {
                            startActivity(Intent(this, ProfileActivity::class.java))
                            true
                        }

                        else -> false
                    }
                }



                sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE)
        val selectedCountry = sharedPreferences.getString("news_country", "us") ?: "us"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        newsCallable = retrofit.create(NewsCallable::class.java)

        setupAdapters()
        setupSearchView()

        fetchNews(selectedCountry, null)

        val categories = mutableListOf(
            Category(R.drawable.favourite_avatar, "Favorites", "favorites"),
            Category(R.drawable.entertainment_ava, "Entertainment", "entertainment"),
            Category(R.drawable.businessman_avatar, "Business", "business"),
            Category(R.drawable.technology_ava, "Technology", "technology"),
            Category(R.drawable.health_avatar, "Health", "health"),
            Category(R.drawable.sports_avatar, "Sports", "sports")
        )
        categoryNews(categories)

        binding.swipeRef.setOnRefreshListener { fetchNews(selectedCountry, null) }

        binding.settingsBtn.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    handleSearchQuery(query.trim())
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun handleSearchQuery(query: String) {
        val countryMap = mapOf(
            "usa" to "us",
            "spain" to "es",
            "egypt" to "eg"
        )

        val categoryList = listOf(
            "business",
            "entertainment",
            "general",
            "health",
            "science",
            "sports",
            "technology"
        )

        val lowerCaseQuery = query.lowercase().trim()

        when {
            countryMap.containsKey(lowerCaseQuery) -> {
                fetchNews(countryMap[lowerCaseQuery]!!, null)
            }

            categoryList.contains(lowerCaseQuery) -> {
                val selectedCountry = sharedPreferences.getString("news_country", "us") ?: "us"
                fetchNews(selectedCountry, lowerCaseQuery)
            }

            else -> {
                searchEverything(lowerCaseQuery)
            }
        }
    }

    private fun fetchNews(country: String, category: String? = null) {
        binding.progressBar.isVisible = true
        Log.d("MainActivity", "Fetching news for country=$country, category=$category")

        newsCallable.getNews(country, category).enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                binding.progressBar.isVisible = false
                binding.swipeRef.isRefreshing = false

                if (!response.isSuccessful || response.body() == null || response.body()?.articles.isNullOrEmpty()) {
                    Toast.makeText(this@MainActivity, "No news found", Toast.LENGTH_SHORT).show()
                    return
                }

                val articles = response.body()!!.articles.toMutableList()
                articles.removeAll { it.title == "[Removed]" || it.urlToImage.isNullOrEmpty() }

                newsAdapter.updateNews(articles)
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to fetch news", Toast.LENGTH_SHORT).show()
                binding.progressBar.isVisible = false
                binding.swipeRef.isRefreshing = false
            }
        })
    }


    private fun searchEverything(query: String) {
        binding.progressBar.isVisible = true
        Log.d("MainActivity", "Searching everything for query=$query")

        newsCallable.searchNews(query).enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                binding.progressBar.isVisible = false
                binding.swipeRef.isRefreshing = false

                if (!response.isSuccessful || response.body() == null || response.body()?.articles.isNullOrEmpty()) {
                    Log.e("MainActivity", "No results found for query: $query")
                    Toast.makeText(this@MainActivity, "No news found", Toast.LENGTH_SHORT).show()
                    return
                }

                val articles = response.body()!!.articles.toMutableList()
                articles.removeAll { it.title == "[Removed]" || it.urlToImage.isNullOrEmpty() }

                newsAdapter.updateNews(articles)
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                Toast.makeText(this@MainActivity, "Failed to fetch news", Toast.LENGTH_SHORT).show()
                binding.progressBar.isVisible = false
                binding.swipeRef.isRefreshing = false
            }
        })
    }

    private fun setupAdapters() {
        newsAdapter = NewsAdapter(this, mutableListOf())
        binding.newsList.adapter = newsAdapter

        categoriesAdapter = CategoriesAdapter(this, mutableListOf()) { selectedCategory ->
            val selectedCountry = sharedPreferences.getString("news_country", "us") ?: "us"
            fetchNews(selectedCountry, selectedCategory)
            binding.categoriesList.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.categoriesList.adapter = categoriesAdapter
    }

    private fun categoryNews(category: MutableList<Category>) {
        val adapter = CategoriesAdapter(this, category) { selectedCategory ->
            if (selectedCategory == "Favorites") {
                val favoritesDb = FavoritesDatabase(this)
                val favorites = favoritesDb.getAllFavorites()
                newsAdapter.updateNews(favorites)
            } else {
                val selectedCountry = sharedPreferences.getString("news_country", "us") ?: "us"
                fetchNews(selectedCountry, selectedCategory)
            }
        }
        binding.categoriesList.adapter = adapter
    }
}
