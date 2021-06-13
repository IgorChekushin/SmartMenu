package com.example.smartmenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartmenu.google_drive_api.GoogleDriveAPI
import com.example.smartmenu.google_drive_api.GoogleDriveAPIImpl
import com.example.smartmenu.google_drive_api.GoogleDriveApiViewModel
import com.example.smartmenu.recipes_list.RecipesActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


private const val TAG = "MainActivity"

private const val REQUEST_SIGN_IN = 1
private const val REQUEST_CODE_OPEN_DOCUMENT = 2
private var testFileList = "1"

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    lateinit var testDataViewModel: GoogleDriveApiViewModel
    lateinit var mDriveServiceHelper: GoogleDriveAPI
    lateinit var singleLiveDataEvent: SingleLiveEvent<Boolean>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestSignIn()
        singleLiveDataEvent = SingleLiveEvent()
        singleLiveDataEvent.observe(this, {
            testDataViewModel.downloadImage("10YD_DZXGsuwKEGqPrbJzFgxXuoiR26SW")
            testDataViewModel.allImages.observe(this, {
                Log.d("SUCCESS", "Count = ${it.count()}")
                Toast.makeText(this, "Count = ${it.count()}", Toast.LENGTH_SHORT).show()
                testImage.setImageBitmap(it[0].second)
            })
        })


//        testDataViewModel = ViewModelProvider(this).get(TestDataViewModel::class.java)
//
//        testDataViewModel.fetchMovies()
//
//        testDataViewModel.testDataLiveData.observe(this, Observer {
//
//            val id = it[0].id;
//            val title = it[0].title
//        })


//        btnFridge.setOnClickListener {
//            val intent = Intent(this, FridgeActivity::class.java)
//            startActivity(intent)
//        }


        bthSearch.setOnClickListener {
            val intent = Intent(this, RecipesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("Tag", "onActivityResult=$requestCode")
        when (requestCode) {
            REQUEST_SIGN_IN -> {
                if (resultCode == RESULT_OK && data != null) {
                    handleSignInResult(data)
                } else {
                    Log.d("Tag", "Signing request failed")
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun buildGoogleSignInClient(): GoogleSignInClient {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // .requestScopes(Drive.SCOPE_FILE)
            // .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .requestScopes(Scope(DriveScopes.DRIVE))
            .build()
        return GoogleSignIn.getClient(this, signInOptions)
    }

    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleAccount ->
                Log.d("Tag", "Signing successful")

                // Use the authenticated account to sign in to the Drive service.
                val credential = GoogleAccountCredential.usingOAuth2(
                    this, listOf(DriveScopes.DRIVE_FILE)
                )
                credential.selectedAccount = googleAccount.account
                val googleDriveService = Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential
                )
                    .setApplicationName(getString(R.string.app_name))
                    .build()
                mDriveServiceHelper = GoogleDriveAPIImpl(googleDriveService)
                testDataViewModel = ViewModelProvider(
                    this,
                    viewModelFactory {
                        GoogleDriveApiViewModel(
                            application,
                            mDriveServiceHelper
                        )
                    }).get(GoogleDriveApiViewModel::class.java)
                Log.d("Tag", "Creating  ViewModel successful")
                testDataViewModel = ViewModelProvider(this).get(GoogleDriveApiViewModel::class.java)
                singleLiveDataEvent.postValue(true)
            }
            .addOnFailureListener { e ->
                Log.e("Tag", "Signing error")
            }
    }

    private fun requestSignIn() {
        val client = buildGoogleSignInClient()
        startActivityForResult(client.signInIntent, REQUEST_SIGN_IN)
    }
}

inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = f() as T
    }