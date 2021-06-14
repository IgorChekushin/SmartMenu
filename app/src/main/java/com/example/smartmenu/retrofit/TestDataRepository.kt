package com.example.smartmenu.retrofit

class TestDataRepository(private val api : TestApi) : BaseRepository() {

    suspend fun getTestData() : MutableList<TestData>?{

        //safeApiCall is defined in BaseRepository.kt (https://gist.github.com/navi25/67176730f5595b3f1fb5095062a92f15)
        val movieResponse = safeApiCall(
            call = {api.getTestDataAsync().await()},
            errorMessage = "Error Fetching Popular Movies"
        )

        return movieResponse?.toMutableList()


    }

}