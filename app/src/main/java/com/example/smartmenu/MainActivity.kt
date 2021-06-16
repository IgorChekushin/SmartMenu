package com.example.smartmenu

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.smartmenu.fridge.FridgeActivity
import com.example.smartmenu.google_drive_api.GoogleDriveAPI
import com.example.smartmenu.google_drive_api.GoogleDriveApiViewModel
import com.example.smartmenu.recipes_list.RecipesActivity
import com.example.smartmenu.retrofit.HerokuViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*



class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
