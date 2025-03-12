package com.example.whatnow

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsCallable {

    @GET("/v2/top-headlines")
    fun getNews(
        @Query("country") country: String,
        @Query("category") category: String? = null,
        @Query("apiKey") apiKey: String = "32e5f65983334984a4de264a697d551b",
        @Query("pageSize") pageSize: Int = 30
    ): Call<News>
    @GET("v2/everything")
    fun searchNews(
        @Query("q") query: String,
        @Query("language") language: String = "en",
        @Query("apiKey") apiKey: String = "32e5f65983334984a4de264a697d551b",
    ): Call<News>
}
/*
maria API key
7bc94ffe944e45cd992b3b12b228f3e2
*/