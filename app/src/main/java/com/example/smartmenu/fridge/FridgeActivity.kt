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
import com.example.smartmenu.retrofit.LoadingIngredientsViewState.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class FridgeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFridgeBinding
    private lateinit var ingredientViewModel: IngredientViewModel
    private lateinit var singleLiveDataEvent2ListView: SingleLiveEvent<Boolean>
    private lateinit var herokuViewModel: HerokuViewModel
    private lateinit var adapter: CustomFridgeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFridgeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.nav_view2)
        bottomNavigation.selectedItemId = R.id.fridge
        bottomNavigation.setOnNavigationItemSelectedListener(navigationBar)

        var listOfFood = mutableListOf<FoodItem>()

        //get actual food list in fridge from SharedPreferences
        val prefs = Prefs(this)

        val singleLiveDataEvent = SingleLiveEvent<Pair<Int, Boolean>>()
        singleLiveDataEvent2ListView = SingleLiveEvent()
        //get recipe ViewModel
        ingredientViewModel = ViewModelProvider(this).get(IngredientViewModel::class.java)
        herokuViewModel = ViewModelProvider(this).get(HerokuViewModel::class.java)
        updateIngredientsDataBase()
        herokuViewModel.loadingIngredientsState.observe(this, {
            when(it){
                is ErrorState -> errorView()
                is LoadingState -> loadingView()
                is LoadedState -> loadedView()
                is NoItemsState -> noItemView()
            }
        })
        //First update list of ingredients
        singleLiveDataEvent2ListView.observe(this, {
            //Second show list of ingredients from db
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
                //listOfFood.sortByDescending { it.isSelected }
                //use adapter in custom ListView
                adapter = CustomFridgeAdapter(listOfFood, singleLiveDataEvent)
                binding.listView.adapter = adapter
            })

        })

        singleLiveDataEvent.observe(this, {
            listOfFood[it.first].isSelected = it.second
            //adapter.setItems(listOfFood)
            adapter.notifyDataSetChanged()
        })
    }

    private fun updateIngredientsDataBase() {
//        val prefs = Prefs(this)
        if (!MyApp.getInstance()?.isUpdate!!) {
            ingredientViewModel = ViewModelProvider(this).get(IngredientViewModel::class.java)
            herokuViewModel = ViewModelProvider(this).get(HerokuViewModel::class.java)
            herokuViewModel.fetchIngredients()
            herokuViewModel.ingredientsLiveData.observe(this, {
                for (ingredient in it) {
                    ingredientViewModel.insert(IngredientEntity(ingredient.name))
                }

                singleLiveDataEvent2ListView.postValue(true)
            })
//            prefs.isUpdated = true
            MyApp.INSTANCE?.isUpdate = true
        } else {

            singleLiveDataEvent2ListView.postValue(true)
        }
    }
    private val navigationBar = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.recipes -> {
                val items: List<String> = adapter.getCheckedItemPositions()
                val prefs = Prefs(this)
                prefs.actualFoodList = items
                finish()
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
    private fun errorView(){
        binding.twLabel.text = getString(R.string.tw_ingredients_loading_error)
        binding.waitGIF2.setImageDrawable(null)
    }
    private fun loadingView(){
        binding.twLabel.text = getString(R.string.tw_ingredients_loading)
        Glide.with(this)
            .asGif()
            .load("file:///android_asset/loading.gif")
            .into(binding.waitGIF2)

    }
    private fun loadedView(){
        binding.twLabel.text = getString(R.string.tw_ingredients_loaded)
        binding.waitGIF2.setImageDrawable(null)
    }
    private fun noItemView(){
        binding.twLabel.text = getString(R.string.tw_ingredients_loading_no_items)
        binding.waitGIF2.setImageDrawable(null)
    }

}

