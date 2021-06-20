package com.example.smartmenu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartmenu.fridge.FridgeActivity
import com.example.smartmenu.recipes_list.RecipesActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*



class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        btnFridge.setOnClickListener {
//            val intent = Intent(this, FridgeActivity::class.java)
//            startActivity(intent)
//        }
//
//
//        bthSearch.setOnClickListener {
//            val intent = Intent(this, RecipesActivity::class.java)
//            startActivity(intent)
//        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.nav_view2)
        bottomNavigation.selectedItemId = R.id.recipes
        bottomNavigation.setOnNavigationItemSelectedListener(navigationBar)
    }


    private val navigationBar = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.recipes -> {
                val intent = Intent(this@MainActivity, RecipesActivity::class.java)
                startActivity(intent)
            }
            R.id.fridge -> {
                val intent = Intent(this@MainActivity, FridgeActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
