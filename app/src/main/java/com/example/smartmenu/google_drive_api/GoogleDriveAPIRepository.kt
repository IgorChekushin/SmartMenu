package com.example.smartmenu.google_drive_api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.smartmenu.db.RecipeEntity
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList

class GoogleDriveAPIRepository(private val googleDriveAPI: GoogleDriveAPI) {
    lateinit var allFoldersLiveData: List<File>

    suspend fun getAllFolders(){
        allFoldersLiveData = googleDriveAPI.getAllFolders()
    }
}