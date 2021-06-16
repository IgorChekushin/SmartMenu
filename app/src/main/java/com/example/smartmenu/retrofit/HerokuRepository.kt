package com.example.smartmenu.retrofit

class HerokuRepository(private val api: HerokuApi) : BaseRepository() {

    suspend fun getAllRecipes(ingredients: List<String>): MutableList<ResponseBody>? {

        val recipesResponse = safeApiCall(
            call = { api.getAllRecipes(ingredients).await() },
            errorMessage = "Error Fetching Popular Movies"
        )

        return recipesResponse?.toMutableList()
    }

}