package com.example.smartmenu.google_drive_api

import android.graphics.Bitmap

class GoogleDriveAPIRepository(private val googleDriveAPI: GoogleDriveAPI) {
    var images : MutableList<Pair<String, Bitmap?>>? = null

    suspend fun getImage(fileId: String){
        val image = googleDriveAPI.getImage(fileId)
        if(image.first != "Error"){
            if(images == null) {
                images = mutableListOf()
            }
            images!!.add(image)
        }
    }
}