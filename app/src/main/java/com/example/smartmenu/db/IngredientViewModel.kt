package com.example.smartmenu.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class IngredientViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: IngredientRepository
    val allIngredients: LiveData<List<IngredientEntity>>

    init {
        val movieDao = IngredientDatabase.getInstance(application).recipeDao()
        repository = IngredientRepository(movieDao)
        allIngredients = repository.allIngredients
    }

    fun insert(ingredientEntity: IngredientEntity) = viewModelScope.launch {
        repository.insert(ingredientEntity)
    }

    fun delete(ingredient: IngredientEntity) = viewModelScope.launch {
        repository.delete(ingredient)
    }
}