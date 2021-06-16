package com.example.smartmenu.retrofit

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

//ApiFactory to create Google Drive Api
object ApiFactory{

    //Creating Auth Interceptor to add api_key query in front of all the requests.
    private val authInterceptor = Interceptor {chain->
        val newUrl = chain.request().url()
            .newBuilder()
            //.addQueryParameter("api_key", AppConstants.tmdbApiKey)
            .build()

        val newRequest = chain.request()
            .newBuilder()
            .url(newUrl)
            .build()

        chain.proceed(newRequest)
    }

    //OkhttpClient for building http request url
    private val herokuClient = OkHttpClient().newBuilder()
        .addInterceptor(authInterceptor)
        .build()



    private fun retrofit() : Retrofit = Retrofit.Builder()
        .client(herokuClient)
        .baseUrl("https://salty-headland-27091.herokuapp.com")
        //.baseUrl("https://jsonplaceholder.typicode.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()


    val herokuApi : HerokuApi = retrofit().create(HerokuApi::class.java)

}