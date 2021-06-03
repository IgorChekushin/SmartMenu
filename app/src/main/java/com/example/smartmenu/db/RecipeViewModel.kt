package com.example.smartmenu.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecipeRepository
    val allRecipes: LiveData<List<RecipeEntity>>

    init {
        val movieDao = RecipeDatabase.getInstance(application).recipeDao()
        repository = RecipeRepository(movieDao)
        allRecipes = repository.allRecipes
    }

    fun insert(movie: RecipeEntity) = viewModelScope.launch {
        repository.insert(movie)
    }

    fun delete(recipe: RecipeEntity) = viewModelScope.launch {
        repository.delete(recipe)
    }
}