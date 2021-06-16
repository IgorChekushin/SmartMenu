package com.example.smartmenu.recipes_list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartmenu.*
import com.example.smartmenu.db.RecipeEntity
import com.example.smartmenu.db.RecipeViewModel
import com.example.smartmenu.google_drive_api.GoogleDriveAPI
import com.example.smartmenu.google_drive_api.GoogleDriveAPIImpl
import com.example.smartmenu.google_drive_api.GoogleDriveApiViewModel
import com.example.smartmenu.retrofit.HerokuViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.android.synthetic.main.activity_recipes.*

private const val REQUEST_SIGN_IN = 1

class RecipesActivity : AppCompatActivity() {
    //private lateinit var vm: RecipeViewModel
    private lateinit var recipeViewModel : HerokuViewModel
    private lateinit var mDriveServiceHelper: GoogleDriveAPI
    lateinit var singleLiveDataEvent: SingleLiveEvent<Boolean>
    lateinit var singleLiveDataEvent2Recycler: SingleLiveEvent<Boolean>
    private lateinit var googleDriveAPIViewModel: GoogleDriveApiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)
        Glide.with(this)
            .asGif()
            .load("file:///android_asset/loading.gif")
            .into(waitGIF)


        requestSignIn()
        val listOfRecipes =
            mutableListOf<Recipe>()

        //get shared preferences
        val prefs = Prefs(this)

        //get recipe view model
        //vm = ViewModelProvider(this).get(RecipeViewModel::class.java)
        recipeViewModel = ViewModelProvider(this).get(HerokuViewModel::class.java)

        val rwSearchResult: RecyclerView = findViewById(R.id.rw_search_result)

        //Single event for Auth Google Drive
        singleLiveDataEvent = SingleLiveEvent()
        singleLiveDataEvent2Recycler = SingleLiveEvent()


        //Get actual recipes
        recipeViewModel.fetchRecipes(prefs.actualFoodList)


        singleLiveDataEvent.observe(this, {
            recipeViewModel.recipesLiveData.observe(this, { items ->

                var countMaxDishes = 0
                var countAllDishes = 0;
                for (it in items) {
                    countAllDishes++
                        val imageName = it.name
                        countMaxDishes++
                        //Download image from GoogleDrive API
                        googleDriveAPIViewModel.downloadImage(it.image_Urls)

                        googleDriveAPIViewModel.allImages.observe(this, { itList ->
                            //Check download image
                            if(itList.any { it.first == "$imageName.png" }) {
                                val image = itList.find { it.first == "$imageName.png" }!!.second
                                if (listOfRecipes.all { it.name != imageName }) {
                                    listOfRecipes.add(Recipe(it.name, it.description, image))
                                    if(countAllDishes == items.count()){
                                        if(listOfRecipes.count()  == countMaxDishes){
                                            //use adapter for custom RecyclerView
                                            singleLiveDataEvent2Recycler.postValue(true)
                                        }
                                    }

                                }
                            }
                        })

                    }

            })
            singleLiveDataEvent2Recycler.observe(this,{
                listOfRecipes.sortBy { it.name }
                waitTextView.text = ""
                waitGIF.setImageDrawable(null)
                rwSearchResult.layoutManager = LinearLayoutManager(this)
                rwSearchResult.adapter = CustomRecipesAdapter(listOfRecipes)
            })

        })
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
                googleDriveAPIViewModel = ViewModelProvider(
                    this,
                    viewModelFactory {
                        GoogleDriveApiViewModel(
                            application,
                            mDriveServiceHelper
                        )
                    }).get(GoogleDriveApiViewModel::class.java)
                Log.d("Tag", "Creating  ViewModel successful")
                googleDriveAPIViewModel =
                    ViewModelProvider(this).get(GoogleDriveApiViewModel::class.java)
                singleLiveDataEvent.postValue(true)
            }
            .addOnFailureListener {
                Log.e("Tag", "Signing error")
            }
    }

    private fun requestSignIn() {
        val client = buildGoogleSignInClient()
        startActivityForResult(client.signInIntent, REQUEST_SIGN_IN)
    }
}

infix fun <T : Any> List<T>.intersects(other: List<T>) = all(other::contains)

inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = f() as T
    }