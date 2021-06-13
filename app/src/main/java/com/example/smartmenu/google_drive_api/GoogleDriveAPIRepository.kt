package com.example.smartmenu.google_drive_api

import android.graphics.Bitmap
import com.google.api.services.drive.model.File

class GoogleDriveAPIRepository(private val googleDriveAPI: GoogleDriveAPI) {
    lateinit var allFolders: List<File>
    var images : MutableList<Pair<String, Bitmap>> = mutableListOf()
    suspend fun getAllFolders(){
        allFolders = googleDriveAPI.getAllFolders()
    }
    suspend fun getImage(fileId: String){
        images.add(googleDriveAPI.getImage(fileId))
    }
}