package com.example.smartmenu.retrofit

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface GDriveApi {
    @GET("posts")
    fun getTestData(): Deferred<Response<List<TestData>>>
}