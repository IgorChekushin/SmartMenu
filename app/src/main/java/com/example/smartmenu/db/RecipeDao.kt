package com.example.smartmenu.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipe_items ORDER BY dish ASC")
    fun getAll(): LiveData<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg recipes: RecipeEntity)

    @Delete
    suspend fun delete(recipe: RecipeEntity)

    @Update
    suspend fun update(vararg recipes: RecipeEntity)
}