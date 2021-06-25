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
import com.example.smartmenu.databinding.ActivityRecipesBinding
import com.example.smartmenu.fridge.FridgeActivity
import com.example.smartmenu.google_drive_api.GoogleDriveAPI
import com.example.smartmenu.google_drive_api.GoogleDriveAPIImpl
import com.example.smartmenu.google_drive_api.GoogleDriveApiViewModel
import com.example.smartmenu.google_drive_api.LoadingImagesState
import com.example.smartmenu.google_drive_api.LoadingImagesState.*
import com.example.smartmenu.retrofit.HerokuViewModel
import com.example.smartmenu.retrofit.set
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

private const val REQUEST_SIGN_IN = 1

class RecipesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipesBinding
    private lateinit var herokuViewModel : HerokuViewModel
    private lateinit var mDriveServiceHelper: GoogleDriveAPI
    private lateinit var singleLiveDataEvent: SingleLiveEvent<Boolean>
    private lateinit var singleLiveDataEvent2Recycler: SingleLiveEvent<Boolean>
    private lateinit var googleDriveAPIViewModel: GoogleDriveApiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.nav_view2)
        bottomNavigation.selectedItemId = R.id.recipes
        bottomNavigation.setOnNavigationItemSelectedListener(navigationBar)

        requestSignIn()
        val listOfRecipes =
            mutableListOf<Recipe>()

        //get shared preferences
        val prefs = Prefs(this)

        //get heroku view model
        herokuViewModel = ViewModelProvider(this).get(HerokuViewModel::class.java)

        val rwSearchResult: RecyclerView = findViewById(R.id.rw_search_result)

        //Single event for Auth Google Drive
        singleLiveDataEvent = SingleLiveEvent()
        singleLiveDataEvent2Recycler = SingleLiveEvent()


        //Get actual recipes
        herokuViewModel.fetchRecipes(prefs.actualFoodList)



        singleLiveDataEvent.observe(this, {
            googleDriveAPIViewModel.loadingImagesState.observe(this, {
                when(it){
                    is ErrorState -> errorView()
                    is LoadingState -> loadingView()
                    is LoadedState -> loadedView()
                    is NoItemsState -> noItemView()
                }
            })

            herokuViewModel.recipesLiveData.observe(this, { items ->
                if(items.count() == 0) {
                    googleDriveAPIViewModel.loadingImagesState.set(NoItemsState)
                }
                var countMaxDishes = 0
                var countAllDishes = 0
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
                                    listOfRecipes.add(Recipe(it.name, it.description, image!!))
                                    if(countAllDishes == items.count()){
                                        if(listOfRecipes.count()  == countMaxDishes){
                                            //use adapter for custom RecyclerView
                                            singleLiveDataEvent2Recycler.postValue(true)
                                            googleDriveAPIViewModel.loadingImagesState.set(
                                                LoadedState
                                            )
                                        }
                                    }
                                }
                            }
                        })
                    }
            })
            singleLiveDataEvent2Recycler.observe(this,{
                listOfRecipes.sortBy { it.name }
                rwSearchResult.layoutManager = LinearLayoutManager(this)
                rwSearchResult.adapter = CustomRecipesAdapter(listOfRecipes)
            })

        })
    }


    private val navigationBar = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.recipes -> {
                val intent = Intent(this@RecipesActivity, RecipesActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.fridge -> {
                val intent = Intent(this@RecipesActivity, FridgeActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
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

    //Render view
    private fun errorView(){
        binding.loadingStateTextView.text = getString(R.string.tw_recipes_loading_error)
        binding.waitGIF.setImageDrawable(null)
    }
    private fun loadingView(){
        binding.loadingStateTextView.text = getString(R.string.tw_recipes_loading)
        Glide.with(this)
            .asGif()
            .load("file:///android_asset/loading.gif")
            .into(binding.waitGIF)

    }
    private fun loadedView(){
        binding.loadingStateTextView.text = getString(R.string.tw_recipes_loaded)
        binding.waitGIF.setImageDrawable(null)
    }
    private fun noItemView(){
        binding.loadingStateTextView.text = getString(R.string.tw_recipes_loading_no_items)
        binding.waitGIF.setImageDrawable(null)
    }
}


inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = f() as T
    }