package com.example.smartmenu.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [IngredientEntity::class], version = 1)
abstract class IngredientDatabase : RoomDatabase() {
    abstract fun recipeDao(): IngredientDao

    companion object {
        @Volatile
        private var INSTANCE: IngredientDatabase? = null

        fun getInstance(context: Context): IngredientDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(IngredientDatabase::class) {
                val instance = Room.databaseBuilder(
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