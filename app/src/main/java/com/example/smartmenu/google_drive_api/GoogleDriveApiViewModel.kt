package com.example.smartmenu.google_drive_api

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.api.services.drive.model.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class GoogleDriveApiViewModel(val app: Application, googleDriveAPI: GoogleDriveAPI) :
    AndroidViewModel(app) {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    var allFolders: MutableLiveData<List<File>> = MutableLiveData()
    var allImages: MutableLiveData<MutableList<Pair<String, Bitmap>>> = MutableLiveData()
    private val repository: GoogleDriveAPIRepository = GoogleDriveAPIRepository(googleDriveAPI)
//init {
//    allImages.postValue(mutableListOf())
//}
    fun fetchFolders() {
        scope.launch {
            repository.getAllFolders()
            allFolders.postValue(repository.allFolders)
        }
    }
    fun downloadImage(fileId: String) {
        scope.launch {
            repository.getImage(fileId)
            allImages.postValue(repository.images)
        }
    }
}

