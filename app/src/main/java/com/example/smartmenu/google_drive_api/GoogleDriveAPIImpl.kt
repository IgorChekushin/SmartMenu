package com.example.smartmenu.google_drive_api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.smartmenu.retrofit.ApiFactory
import com.example.smartmenu.retrofit.TestDataRepository
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class GoogleDriveAPIImpl(private val mDriveService: Drive) : GoogleDriveAPI {
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)


    override suspend fun getAllFolders(): MutableList<File> =
        mDriveService.files().list().setSpaces("drive").execute().files


    override suspend fun getImage(fileId: String): Pair<String, Bitmap> {
        val metadata = mDriveService.files().get(fileId).execute()

        val inputStream = mDriveService.files().get(fileId).executeMediaAsInputStream().readBytes()
        val image = BitmapFactory.decodeByteArray(inputStream, 0, inputStream.size)
        return Pair(metadata.name, image)
    }
}