package com.example.smartmenu.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [IngredientEntity::class], version = 1)
abstract class IngredientDatabase : RoomDatabase() {
    abstract fun recipeDao(): IngredientDao
    //Создать наследник Application и взять у него applicationContext
    //class MyApplication : Application()

    companion object {
        @Volatile
        private var INSTANCE: IngredientDatabase? = null

        fun getInstance(context: Context): IngredientDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            //val myApp = MyApplication()
            //val myCtx = myApp.applicationContext
            synchronized(IngredientDatabase::class) {
                val instance = Room.databaseBuilder(
                    //myCtx,
                    context.applicationContext,
                    IngredientDatabase::class.java,
                    "ingredient_database"
                )
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}