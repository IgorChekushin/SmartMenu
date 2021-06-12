package com.example.smartmenu.google_drive_api

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartmenu.db.RecipeDatabase
import com.example.smartmenu.db.RecipeEntity
import com.example.smartmenu.db.RecipeRepository
import com.example.smartmenu.retrofit.ApiFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
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
    private val repository: GoogleDriveAPIRepository = GoogleDriveAPIRepository(googleDriveAPI)

    init {
//       // val googleDriveAPI = GoogleDriveAPIFactory(app).googleDriveAPIImpl
//        val repository = GoogleDriveAPIRepository(googleDriveAPI)

        //    }
    }

    fun fetchFolders() {
        scope.launch {
            repository.getAllFolders()
            allFolders.postValue(repository.allFoldersLiveData)
        }
    }
}

