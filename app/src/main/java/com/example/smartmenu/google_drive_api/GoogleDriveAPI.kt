package com.example.smartmenu.google_drive_api

import androidx.lifecycle.LiveData
import com.example.smartmenu.db.RecipeEntity
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList

interface GoogleDriveAPI {
    suspend fun getAllFolders(): List<File>
}