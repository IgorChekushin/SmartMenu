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
    private lateinit var vm: RecipeViewModel
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
        vm = ViewModelProvider(this).get(RecipeViewModel::class.java)
        val rwSearchResult: RecyclerView = findViewById(R.id.rw_search_result)

        //Single event for Auth Google Drive
        singleLiveDataEvent = SingleLiveEvent()
        singleLiveDataEvent2Recycler = SingleLiveEvent()

        //update list of recipes if data base was update
        singleLiveDataEvent.observe(this, {
            vm.allRecipes.observe(this, { items ->
                if (items.isEmpty()) {

                    vm.insert(
                        RecipeEntity(
                            "Brisket (pork),Eggplant,Zucchini,Onion,Garlic (large),Breadcrumbs,Honey,Sweet paprika,Vinegar,Black pepper,Soy sauce,Vegetable oil",
                            "Brisket with vegetables in Korean",
                            "Prepare the food, wash the vegetables, cut the brisket. Heat the vegetable oil in a wide frying pan, grease the entire bottom with it. Fry the brisket until nicely browned, 5-7 minutes, over medium heat. Add the onion feathers and cook for 3 minutes, stirring. Sprinkle with breadcrumbs or starch, fry for another couple of minutes. Add eggplant cubes and garlic slices. Fry for a couple of minutes over medium heat. Zucchini cut into cubes, add, fry for 3-5 minutes. Pour in the soy sauce, honey, sprinkle with paprika, ground black pepper, stir, fry for a couple of minutes, pour in the vinegar, stir and remove from the heat. If desired, add salt to your taste. Traditionally served with boiled unleavened rice and a spicy Korean snack.",
                            "1PTdBAnpWL4MdPCshiez9XiyMmaWZeChA"
                        )
                    )
                    vm.insert(
                        RecipeEntity(
                            "Pork,Bacon,Mozzarella,Salt,Black pepper",
                            "Pork Medallions",
                            "Wash the pork tenderloin, dry it with a paper towel and remove excess films and tendons. Cut across the fibers into portions, 4-5 cm thick. In the middle of each piece, make an incision to make a \"pocket\" in which to put a cube of cheese. Wrap with a strip of bacon and fasten with a toothpick to preserve the shape of the medallions. Fry in vegetable oil for 2 minutes on both sides. Add salt and pepper to taste.. Put it in a heat-resistant form and send it to the oven at a temperature of 190 ° C for 10 minutes. Let the finished medallions rest for 5 minutes, cut into 2 pieces or serve whole.",
                            "15zDt3WjwdvJIvqbAdeeLR15zvXNi97l_"
                        )
                    )
                    vm.insert(
                        RecipeEntity(
                            "Pasta,Beef,Onion,Butter,Broth,Black pepper,Salt",
                            "Navy macaroni",
                            "Melt half the butter in a frying pan and fry the chopped onions until transparent. Add the minced meat from the boiled beef. Pour in a little beef broth left over from cooking the meat. Season with salt and pepper. Put the boiled pasta. Add the remaining butter and combine with the minced meat.",
                            "1as5N1p__aABG4Sm4Ba5VaGzbOe29oHzS"
                        )
                    )
                    vm.insert(
                        RecipeEntity(
                            "Broth,Onion,Carrot,Zucchini,Pasta,Green peas,Black pepper,Salt,Vegetable oil",
                            "Diet vegetable Soup",
                            "The breast can be boiled in advance or directly directly when preparing the soup. We will need 400 ml of unsalted broth. Cut off 40 g of carrots. Pour the broth into a saucepan and bring to a boil. Very finely chop the onion and small cubes of carrots. We send it to the boiling broth and cook for 10 minutes. Next, add the zucchini, cut into small cubes. Cook the soup for another 5 minutes. Next, add the pasta and green peas. Salt the soup to taste and put a couple of peas of black pepper. Cook until the pasta is ready. This is another 5 minutes. Pour the oil into the soup and immediately remove from the heat.",
                            "1Jpcukf1KK4jWKA_VZ3LeS6pL_zjeUAnt"
                        )
                    )
                    vm.insert(
                        RecipeEntity(
                            "Chicken fillet,Champignons,Hard cheese,Greens,Garlic,Sour cream,Mayonnaise,Chicken egg",
                            "Juicy chicken fillet rolls in the oven",
                            "Fillet cut into pieces, beat with a hammer, salt and pepper to taste. We prepare the filling, finely chop the mushrooms, finely chop the greens, cheese on a fine grater, garlic through the crush, add sour cream and mayonnaise, everything is well placed.Put 1 tbsp of dressing in the meat and roll it into a tube, put it in a baking sheet with the seam down.Beat the eggs, lubricate the rolls.We send it to the oven for 30 minutes at 200 degrees.",
                            "1r0J6xJQNOTAe--dr_yE-UzzT8IMCcVVp"
                        )
                    )
                    vm.insert(
                        RecipeEntity(
                            "Milk,Chicken egg,Salt,Yeast,Flour",
                            "Yeast pancakes with milk",
                            "Preheat 1 liter of milk to a warm state.Raw yeast is poured with a small amount of warm milk.We dissolve the yeast so that there are no lumps.In the milk, we break the eggs. Add salt, diluted yeast.Mix well.Gradually add 700-750 grams of sifted flour. Knead the dough.You can add sugar to the dough as desired.Cover the dough with a lid and leave it for about 1 hour in a warm place to rise.The dough is ready, you can bake pancakes.Heat the pan well.Pour vegetable oil.Fry the pancakes on both sides until a beautiful golden color under a closed lid.Ready-made pancakes are greased with butter and sprinkle with sugar.",
                            "10b-B4g434y-iEhKi93MgzuJB8cxk2ISj"
                        )
                    )
                    vm.insert(
                        RecipeEntity(
                            "Apple,Cottage cheese,Flour,Chicken egg,Sugar,Vanillin,Baking powder,Butter",
                            "Cottage cheese charlotte",
                            "Beat the eggs with sugar well until A THICK FOAM.Add the sifted flour with baking powder and vanilla to the mixture. Once again, beat a little until smooth.Enter the cottage cheese and mash it well with a fork and actively mix it into the dough. Do not whisk!Peel the apples, cut them into small cubes and add them to the dough.Mix and place in a well-greased baking dish.Bake in a preheated 180° - 185°C oven for about 20 minutes.",
                            "15Og3UGiIW0rtJ6x2GPQhjqgSwMp5YSzY"
                        )
                    )
                    vm.insert(
                        RecipeEntity(
                            "Chicken,Potato,Onion,Sweet paprika,Black pepper,Lemon,Salt,Vegetable oil",
                            "Chicken and potatoes",
                            "AWash the ham and dry it. Peel the onion.Fry the ham on both sides in vegetable oil until golden brown, 5-7 minutes, medium heat.\n" +
                                    "Add the onion feathers, fry for 3 minutes, medium heat.Add paprika, pepper, lemon slices, and cook over low heat for 5 minutes.Add salt and simmer for 5 minutes.Add the potato slices, cover with a lid, bring to a boil over low heat, and mix gently.",
                            "125YEF0erpXQe1IMigyWnW7lzaG1pgepq"
                        )
                    )

                }
                var countMaxDishes = 0
                var countAllDishes = 0;
                for (it in items) {
                    countAllDishes++
                    if (it.ingredients.split(",") intersects prefs.actualFoodList) {
                        val imageName = it.dish
                        countMaxDishes++
                        //Download image from GoogleDrive API
                        googleDriveAPIViewModel.downloadImage(it.image)

                        googleDriveAPIViewModel.allImages.observe(this, { itList ->
                            //Check download image
                            if(itList.any { it.first == "$imageName.png" }) {
                                val image = itList.find { it.first == "$imageName.png" }!!.second
                                if (listOfRecipes.all { it.name != imageName }) {
                                    listOfRecipes.add(Recipe(it.dish, it.description, image))
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