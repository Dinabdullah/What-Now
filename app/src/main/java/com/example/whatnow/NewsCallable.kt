package com.example.whatnow

import retrofit2.Call
import retrofit2.http.GET

interface NewsCallable {

    @GET("/v2/top-headlines?country=us&category=general&apiKey=f5fd9135e7a24c61886b994c64655e9b&pageSize=30")
    fun getNews(): Call<News>
}