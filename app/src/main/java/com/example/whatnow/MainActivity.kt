package com.example.whatnow

import android.os.Bundle
import android.util.Log
import android.view.View
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

        fetchNews(null)
        val categories = mutableListOf(
            Category(
                R.drawable.entertainment, "Entertainment", "entertainment"),
            Category(R.drawable.business, "Business", "business"),
            Category(R.drawable.technology, "Technology", "technology"),
            Category(R.drawable.health, "Health", "health"),
            Category(R.drawable.football_icon, "Sports", "sports")
        )
        categoryNews(categories)


        binding.swipeRef.setOnRefreshListener { fetchNews(null) }
    }


    private fun fetchNews(category: String? = null) {
        binding.progressBar.isVisible = true

        newsCallable.getNews(category).enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                binding.progressBar.isVisible = false
                binding.swipeRef.isRefreshing = false

                if (!response.isSuccessful || response.body() == null || response.body()?.articles.isNullOrEmpty()) {
                    Log.e("MainActivity", "Response unsuccessful or articles are null")
                    return
                }

                val articles = response.body()!!.articles.toMutableList()
                articles.removeAll { it.title == "[Removed]" || it.urlToImage == null }

                Log.d("trace", "Data: $articles")
                newsAdapter.updateNews(articles)
                binding.categoriesList.visibility = View.VISIBLE
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                binding.progressBar.isVisible = false
                binding.swipeRef.isRefreshing = false
            }
        })
    }


    private fun setupAdapters() {

        newsAdapter = NewsAdapter(this, mutableListOf())
        binding.newsList.adapter = newsAdapter


        categoriesAdapter = CategoriesAdapter(this, mutableListOf()) { selectedCategory ->
            fetchNews(selectedCategory)
            binding.categoriesList.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        }

        binding.categoriesList.adapter = categoriesAdapter

    }

    private fun categoryNews(category: MutableList<Category>) {
        val adapter = CategoriesAdapter(this, category) { selectedCategory ->
            fetchNews(selectedCategory)
        }
        binding.categoriesList.adapter = adapter
    }


}
