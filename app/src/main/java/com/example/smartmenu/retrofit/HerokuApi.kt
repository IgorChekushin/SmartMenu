package com.example.smartmenu.retrofit

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HerokuApi {
//    @GET("posts")
//    fun getTestDataAsync(): Deferred<Response<List<TestData>>>
@POST("recipes/getAllRecipes")
fun getAllRecipesAsync(@Body requestBody: List<String>): Deferred<Response<List<ResponseRecipeBody>>>

@GET("recipes/getAllIngredients")
fun getAllIngredientsAsync():Deferred<Response<List<ResponseIngredientsBody>>>
}