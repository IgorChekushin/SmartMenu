package com.example.smartmenu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.smartmenu.fridge.FridgeActivity
import com.example.smartmenu.recipes_list.RecipesActivity
import com.example.smartmenu.retrofit.TestDataViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var testdataViewModel: TestDataViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        testdataViewModel = ViewModelProviders.of(this).get(TestDataViewModel::class.java)

        testdataViewModel.fetchMovies()

        testdataViewModel.testDataLiveData.observe(this, Observer {

            val id = it[0].id;
            val title = it[0].title
        })


        btnFridge.setOnClickListener {
            val intent = Intent(this, FridgeActivity::class.java)
            startActivity(intent)
        }

        bthSearch.setOnClickListener {
            val intent = Intent(this, RecipesActivity::class.java)
            startActivity(intent)
        }
    }
}