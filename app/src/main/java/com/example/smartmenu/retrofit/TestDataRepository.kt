package com.example.smartmenu.retrofit

class TestDataRepository(private val api : GDriveApi) : BaseRepository() {

    suspend fun getTestData() : MutableList<TestData>?{

        //safeApiCall is defined in BaseRepository.kt (https://gist.github.com/navi25/67176730f5595b3f1fb5095062a92f15)
        val movieResponse = safeApiCall(
            call = {api.getTestData().await()},
            errorMessage = "Error Fetching Popular Movies"
        )

        return movieResponse?.toMutableList();


    }

}