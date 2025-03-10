package com.example.whatnow

data class News(
    val articles: ArrayList<Article>,
)

data class Article(
    val title: String,
    val url: String,
    val urlToImage: String,
)

data class Category(
    val image: Int,
    val categoryTitle: String,
    val url: String,
)
