package com.example.smartmenu.retrofit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class HerokuViewModel : ViewModel(){

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository : HerokuRepository = HerokuRepository(ApiFactory.herokuApi)

    val recipesLiveData = MutableLiveData<MutableList<ResponseBody>>()

    fun fetchRecipes(ingredients : List<String>){
        scope.launch {
            val recipes = repository.getAllRecipes(ingredients)
            recipesLiveData.postValue(recipes!!)
        }
    }


    fun cancelAllRequests() = coroutineContext.cancel()

}