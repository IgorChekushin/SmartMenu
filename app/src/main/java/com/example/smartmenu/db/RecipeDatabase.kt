package com.example.smartmenu.db

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecipeEntity::class], version = 1)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    //Создать наследник Application и взять у него applicationContext
    //class MyApplication : Application()

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        fun getInstance(context: Context): RecipeDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            //val myApp = MyApplication()
            //val myCtx = myApp.applicationContext
            synchronized(RecipeDatabase::class) {
                val instance = Room.databaseBuilder(
                    //myCtx,
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "recipe_database"
                )
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}