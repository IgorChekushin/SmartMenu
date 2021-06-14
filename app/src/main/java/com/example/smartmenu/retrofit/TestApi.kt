package com.example.smartmenu.retrofit

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface TestApi {
    @GET("posts")
    fun getTestDataAsync(): Deferred<Response<List<TestData>>>
}