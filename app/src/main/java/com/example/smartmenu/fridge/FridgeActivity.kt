package com.example.smartmenu.fridge


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.smartmenu.*
import com.example.smartmenu.databinding.ActivityFridgeBinding
import com.example.smartmenu.db.IngredientEntity
import com.example.smartmenu.db.IngredientViewModel
import com.example.smartmenu.recipes_list.RecipesActivity
import com.example.smartmenu.retrofit.HerokuViewModel
import com.example.smartmenu.view_states.LoadingViewState
import com.example.smartmenu.view_states.LoadingViewState.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class FridgeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFridgeBinding
    private lateinit var ingredientViewModel: IngredientViewModel
    private lateinit var updateDataBaseEvent: SingleLiveEvent<Boolean>
    private lateinit var adapterEvent: SingleLiveEvent<Pair<Int, Boolean>>
    private lateinit var herokuViewModel: HerokuViewModel
    private lateinit var adapter: CustomFridgeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        //binding
        super.onCreate(savedInstanceState)
        binding = ActivityFridgeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //config navigation bar
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.nav_view2)
        bottomNavigation.selectedItemId = R.id.fridge
        bottomNavigation.setOnNavigationItemSelectedListener(navigationBar)

        //List of ingredients
        var listOfFood = mutableListOf<FoodItem>()

        //get actual food list in fridge from SharedPreferences
        val prefs = Prefs(this)

        //initialise events
        adapterEvent = SingleLiveEvent()
        updateDataBaseEvent = SingleLiveEvent()

        //initialise ViewModel
        ingredientViewModel = ViewModelProvider(this).get(IngredientViewModel::class.java)
        herokuViewModel = ViewModelProvider(this).get(HerokuViewModel::class.java)


        updateIngredientsDataBase()
        herokuViewModel.loadingIngredientsState.observe(this, {
            render(it)
        })
        //update list of ingredients
        updateDataBaseEvent.observe(this, {
            //show list of ingredients from db
            ingredientViewModel.allIngredients.observe(this, { items ->

                //get all ingredients from db
                for (it in items) {
                    val ingredients = it.ingredients.split(",")
                    for (ingredient in ingredients)
                        listOfFood.add(FoodItem(ingredient, false))
                }
                listOfFood = listOfFood.distinctBy { it.foodName }.toMutableList()

                //check and sort ingredients from preferences
                for (ing in listOfFood) {
                    for (actIng in prefs.actualFoodList) {
                        if (actIng == ing.foodName) {
                            ing.isSelected = true
                        }
                    }
                }
                listOfFood.sortBy { it.foodName }

                //use adapter in custom ListView
                adapter = CustomFridgeAdapter(listOfFood, adapterEvent)
                binding.listView.adapter = adapter
            })

        })

        adapterEvent.observe(this, {
            listOfFood[it.first].isSelected = it.second
            adapter.notifyDataSetChanged()
        })
    }

    private fun updateIngredientsDataBase() {
        if (!MyApp.getInstance()?.isUpdate!!) {
            herokuViewModel.fetchIngredients()
            herokuViewModel.ingredientsLiveData.observe(this, {
                for (ingredient in it) {
                    ingredientViewModel.insert(IngredientEntity(ingredient.name))
                }
                updateDataBaseEvent.postValue(true)
            })
            MyApp.INSTANCE?.isUpdate = true
        } else {
            updateDataBaseEvent.postValue(true)
        }
    }

    private val navigationBar = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.recipes -> {
                val items: List<String> = adapter.getCheckedItemPositions()
                val prefs = Prefs(this)
                prefs.actualFoodList = items
                val intent = Intent(this, RecipesActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.fridge -> {
                val intent = Intent(this@FridgeActivity, FridgeActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    //Render view
    private fun render(state: LoadingViewState) {
        when (state) {
            is ErrorState -> errorView()
            is LoadingState -> loadingView()
            is LoadedState -> loadedView()
            is NoItemsState -> noItemView()
        }
    }

    private fun errorView() {
        binding.twLabel.text = getString(R.string.tw_ingredients_loading_error)
        binding.waitGIF2.setImageDrawable(null)
    }

    private fun loadingView() {
        binding.twLabel.text = getString(R.string.tw_ingredients_loading)
        Glide.with(this)
            .asGif()
            .load("file:///android_asset/loading.gif")
            .into(binding.waitGIF2)
    }

    private fun loadedView() {
        binding.twLabel.text = getString(R.string.tw_ingredients_loaded)
        binding.waitGIF2.setImageDrawable(null)
    }

    private fun noItemView() {
        binding.twLabel.text = getString(R.string.tw_ingredients_loading_no_items)
        binding.waitGIF2.setImageDrawable(null)
    }
}