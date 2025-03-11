package com.example.whatnow

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatnow.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var newsCallable: NewsCallable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        newsCallable = retrofit.create(NewsCallable::class.java)

        setupAdapters()
        setupSearchView()

        fetchNews("us", null)

        val categories = mutableListOf(
            Category(R.drawable.entertainment, "Entertainment", "entertainment"),
            Category(R.drawable.business, "Business", "business"),
            Category(R.drawable.technology, "Technology", "technology"),
            Category(R.drawable.health, "Health", "health"),
            Category(R.drawable.football_icon, "Sports", "sports")
        )
        categoryNews(categories)

        binding.swipeRef.setOnRefreshListener { fetchNews("us", null) }
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
            "united states" to "us",
            "uk" to "gb",
            "germany" to "de",
            "france" to "fr",
            "india" to "in",
            "canada" to "ca",
            "australia" to "au"
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
                fetchNews("us", lowerCaseQuery)
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
                    Log.e(
                        "MainActivity",
                        "No results found for: country=$country, category=$category"
                    )
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
            fetchNews("us", selectedCategory)
            binding.categoriesList.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.categoriesList.adapter = categoriesAdapter
    }

    private fun categoryNews(category: MutableList<Category>) {
        val adapter = CategoriesAdapter(this, category) { selectedCategory ->
            fetchNews("us", selectedCategory)
        }
        binding.categoriesList.adapter = adapter
    }
}
