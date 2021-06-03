package com.example.smartmenu.db

import androidx.lifecycle.LiveData

class RecipeRepository(private val recipeDao: RecipeDao) {
    val allRecipes: LiveData<List<RecipeEntity>> = recipeDao.getAll()

    suspend fun insert(recipe: RecipeEntity) {
        recipeDao.insert(recipe)
    }

    suspend fun delete(recipe: RecipeEntity) = recipeDao.delete(recipe)
}