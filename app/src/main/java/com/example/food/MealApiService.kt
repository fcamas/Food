package com.example.food

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface MealApiService {
    @GET("filter.php")
    fun getMealsByCategory(@Query("c") category: String): Call<MealResponse>
}