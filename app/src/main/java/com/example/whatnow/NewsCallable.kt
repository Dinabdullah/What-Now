package com.example.whatnow

import retrofit2.Call
import retrofit2.http.GET

interface NewsCallable {

    @GET("/v2/top-headlines?country=us&apiKey=7bc94ffe944e45cd992b3b12b228f3e2")
    fun getNews(
        @Query("category") category: String? = null,): Call<News>
}
