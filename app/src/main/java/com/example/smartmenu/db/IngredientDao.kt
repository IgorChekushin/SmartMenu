package com.example.smartmenu.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface IngredientDao {
    @Query("SELECT * FROM ingredients_items ORDER BY ingredient ASC")
    fun getAll(): LiveData<List<IngredientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg ingredients: IngredientEntity)

    @Delete
    suspend fun delete(ingredient: IngredientEntity)

    @Update
    suspend fun update(vararg ingredients: IngredientEntity)
}