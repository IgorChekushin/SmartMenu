package com.example.smartmenu.google_drive_api

import android.graphics.Bitmap
import com.google.api.services.drive.model.File

interface GoogleDriveAPI {
    suspend fun getAllFolders(): List<File>
    suspend fun getImage(fileId: String): Pair<String, Bitmap?>
}