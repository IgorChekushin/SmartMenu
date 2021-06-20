package com.example.smartmenu.retrofit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class HerokuViewModel : ViewModel() {

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository: HerokuRepository = HerokuRepository(ApiFactory.herokuApi)

    val recipesLiveData = MutableLiveData<MutableList<ResponseRecipeBody>>()
    val ingredientsLiveData = MutableLiveData<MutableList<ResponseIngredientsBody>>()
    val loadingIngredientsState =
        MutableLiveData<LoadingIngredientsViewState>().default(LoadingIngredientsViewState.LoadedState)


    fun fetchRecipes(ingredients: List<String>) {
        scope.launch {
            val recipes = repository.getAllRecipes(ingredients)
            recipesLiveData.postValue(recipes!!)
        }
    }



fun fetchIngredients() {
    scope.launch {
        val ingredients = repository.getAllIngredients()
        if (ingredients == null) {
            launch(Dispatchers.Main) {
                loadingIngredientsState.set(LoadingIngredientsViewState.ErrorState)
            }
        } else {
            if (ingredients.count() == 0) {
                launch(Dispatchers.Main) {
                    loadingIngredientsState.set(LoadingIngredientsViewState.NoItemsState)
                }
            } else {
                launch(Dispatchers.Main) {
                    loadingIngredientsState.set(LoadingIngredientsViewState.LoadedState)
                }
                ingredientsLiveData.postValue(ingredients!!)
            }
        }
    }
}


fun cancelAllRequests() = coroutineContext.cancel()

}

fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }
fun <T> MutableLiveData<T>.set(newValue: T) = apply { setValue(newValue) }