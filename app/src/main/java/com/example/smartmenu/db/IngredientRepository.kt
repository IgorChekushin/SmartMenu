package com.example.smartmenu.db

import androidx.lifecycle.LiveData

class IngredientRepository(private val ingredientDao: IngredientDao) {
    val allIngredients: LiveData<List<IngredientEntity>> = ingredientDao.getAll()

    suspend fun insert(ingredient: IngredientEntity) {
        ingredientDao.insert(ingredient)
    }

    suspend fun delete(ingredient: IngredientEntity) = ingredientDao.delete(ingredient)
}