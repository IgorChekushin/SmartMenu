package com.example.smartmenu.retrofit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class TestDataViewModel : ViewModel(){

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository : TestDataRepository = TestDataRepository(ApiFactory.GOOGLE_DRIVE_API)


    val testDataLiveData = MutableLiveData<MutableList<TestData>>()

    fun fetchMovies(){
        scope.launch {
            val popularMovies = repository.getTestData()
            testDataLiveData.postValue(popularMovies!!)
        }
    }


    fun cancelAllRequests() = coroutineContext.cancel()

}