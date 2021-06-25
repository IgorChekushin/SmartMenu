package com.example.smartmenu.google_drive_api

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.smartmenu.retrofit.default
import com.example.smartmenu.retrofit.set
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class GoogleDriveApiViewModel(val app: Application, googleDriveAPI: GoogleDriveAPI) :
    AndroidViewModel(app) {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    var allImages: MutableLiveData<MutableList<Pair<String, Bitmap?>>> = MutableLiveData()
    private val repository: GoogleDriveAPIRepository = GoogleDriveAPIRepository(googleDriveAPI)
    val loadingImagesState =
        MutableLiveData<LoadingImagesState>().default(LoadingImagesState.LoadingState)

    fun downloadImage(fileId: String) {
        scope.launch {
            repository.getImage(fileId)

            if (repository.images == null) {
                launch(Dispatchers.Main) {
                    loadingImagesState.set(LoadingImagesState.ErrorState)
                }
            } else {
                allImages.postValue(repository.images!!)
            }
        }
    }
}

