package com.example.smartmenu.google_drive_api

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.Executors


/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
class DriveServiceHelper(val mDriveService: Drive) {

    /**
     * Creates a text file in the user's My Drive folder and returns its file ID.
     */
    fun createFile() = Tasks.call {
        val metadata = File()
            .setParents(Collections.singletonList("root"))
            .setMimeType("image/png")
            .setName("UntitledFile")
        val googleFile = mDriveService.files().create(metadata).execute()
        return@call googleFile.id
    }

    /**
     * Opens the file identified by [fileId] and returns a [Pair] of its name and
     * contents.
     */
    fun readFile(fileId: String) = Tasks.call {
        val metadata = mDriveService.files().get(fileId).execute()

        val inputStream = mDriveService.files().get(fileId).executeMediaAsInputStream()
        val inputString = inputStream.bufferedReader().use { it.readText() }

        return@call Pair(metadata.name, inputString)
    }

    /**
     * Updates the file identified by [fileId] with the given [name] and
     * [content].
     */
    fun saveFile(fileId: String, name: String, content: String) = Tasks.call {
        val metadata = File().setName(name)
        val contentStream = ByteArrayContent.fromString("image/png", content)

        mDriveService.files().update(fileId, metadata, contentStream).execute()
    }


    /**
     * Returns a FileList containing all the visible files in the user's My Drive.
     */
    fun queryFiles() = mDriveService.files().list().setSpaces("drive").execute()


    /**
     * Returns an [Intent] for opening the Storage Access Framework file picker.
     */
    fun createFilePickerIntent(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/png"
        return intent
    }

    /**
     * Opens the file at the `uri` returned by a Storage Access Framework [Intent]
     * created by [.createFilePickerIntent] using the given `contentResolver`.
     */
    fun openFileUsingStorageAccessFramework(
        contentResolver: ContentResolver, uri: Uri?
    ) = Tasks.call {

        // Retrieve the document's display name from its metadata.
        var name: String
        contentResolver.query(uri!!, null, null, null, null).use { cursor ->
            name = if (cursor != null && cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.getString(nameIndex)
            } else {
                throw IOException("Empty cursor returned for file.")
            }
        }

        // Read the document's contents as a String.
        var content: String
        contentResolver.openInputStream(uri).use { `is` ->
            BufferedReader(InputStreamReader(`is`)).use { reader ->
                val stringBuilder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
                content = stringBuilder.toString()
            }
        }
        Pair(name, content)
    }

}
