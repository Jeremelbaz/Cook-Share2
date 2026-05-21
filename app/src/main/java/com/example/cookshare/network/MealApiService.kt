package com.example.cookshare.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

data class MealResponse(
    val meals: List<Meal>?
)

data class Meal(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String,
    val strArea: String,
    val strInstructions: String,
    val strMealThumb: String
)

interface MealApiService {

    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): Response<MealResponse>

    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): Response<MealResponse>
}