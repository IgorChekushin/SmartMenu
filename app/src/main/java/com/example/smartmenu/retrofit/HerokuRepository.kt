package com.example.smartmenu.retrofit

class HerokuRepository(private val api: HerokuApi) : BaseRepository() {

    suspend fun getAllRecipes(ingredients: List<String>): MutableList<ResponseRecipeBody>? {

        val recipesResponse = safeApiCall(
            call = { api.getAllRecipes(ingredients).await() },
            errorMessage = "Error Fetching Popular Movies"
        )

        return recipesResponse?.toMutableList()
    }

    suspend fun getAllIngredients(): MutableList<ResponseIngredientsBody>? {

        val ingredientsResponse = safeApiCall(
            call = { api.getAllIngredients().await() },
            errorMessage = "Error Fetching Popular Movies"
        )
        return ingredientsResponse?.toMutableList()
    }
}