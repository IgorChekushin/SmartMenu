package com.example.smartmenu.google_drive_api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File

class GoogleDriveAPIImpl(private val mDriveService: Drive) : GoogleDriveAPI {

    override suspend fun getAllFolders(): MutableList<File> =
        mDriveService.files().list().setSpaces("drive").execute().files


    override suspend fun getImage(fileId: String): Pair<String, Bitmap?> {
        val metadata = mDriveService.files().get(fileId).execute()
        val inputStream = mDriveService.files().get(fileId).executeMediaAsInputStream().readBytes()
        val image = BitmapFactory.decodeByteArray(inputStream, 0, inputStream.size)
        return if(metadata != null && image !=null)
            Pair(metadata.name, image)
        else
            Pair(
                "Error",
                null
            )
    }
}