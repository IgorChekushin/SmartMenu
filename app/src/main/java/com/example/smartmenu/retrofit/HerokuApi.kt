package com.example.smartmenu.retrofit

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface HerokuApi {
//    @GET("posts")
//    fun getTestDataAsync(): Deferred<Response<List<TestData>>>
@POST("recipes/getAllRecipes")
fun getAllRecipes(@Body requestBody: List<String>): Deferred<Response<List<ResponseBody>>>
}