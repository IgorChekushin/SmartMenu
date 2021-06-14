package com.example.smartmenu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartmenu.fridge.FridgeActivity
import com.example.smartmenu.google_drive_api.GoogleDriveAPI
import com.example.smartmenu.google_drive_api.GoogleDriveApiViewModel
import com.example.smartmenu.recipes_list.RecipesActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*



class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    lateinit var testDataViewModel: GoogleDriveApiViewModel
    lateinit var singleLiveDataEvent: SingleLiveEvent<Boolean>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



//        testDataViewModel = ViewModelProvider(this).get(TestDataViewModel::class.java)
//
//        testDataViewModel.fetchMovies()
//
//        testDataViewModel.testDataLiveData.observe(this, Observer {
//
//            val id = it[0].id;
//            val title = it[0].title
//        })


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
