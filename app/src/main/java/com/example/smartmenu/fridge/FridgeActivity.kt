package com.example.smartmenu.fridge


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.smartmenu.*
import com.example.smartmenu.db.RecipeViewModel
import kotlinx.android.synthetic.main.activity_fridge.*

class FridgeActivity : AppCompatActivity() {
    private lateinit var vm: RecipeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fridge)

        var listOfFood = mutableListOf<FoodItem>()
        lateinit var adapter: CustomFridgeAdapter
        //get actual food list in fridge from SharedPreferences
        val prefs = Prefs(this)


        lateinit var singleLiveDataEvent: SingleLiveEvent<Pair<Int, Boolean>>


        //get recipe ViewModel
        vm = ViewModelProvider(this).get(RecipeViewModel::class.java)
        vm.allRecipes.observe(this, Observer { items ->

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
            listView.adapter = adapter
        })


        singleLiveDataEvent = SingleLiveEvent()
        singleLiveDataEvent.observe(this, Observer {
            listOfFood[it.first].isSelected = it.second
            //adapter.setItems(listOfFood)
            adapter.notifyDataSetChanged()
        })


        //update SharedPreferences
        var items: List<String>
        btnUpdateFridge.setOnClickListener {
            items = adapter.getCheckedItemPositions()
            val prefs = Prefs(it.context)
            prefs.actualFoodList = items
            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

}

