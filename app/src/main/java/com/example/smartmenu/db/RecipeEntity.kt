package com.example.smartmenu.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_items")
data class RecipeEntity(
    @ColumnInfo(name = "ingredients") var ingredients: String,
    @ColumnInfo(name = "dish") var dish: String,
    @ColumnInfo(name = "Description") var description: String,
    @ColumnInfo(name = "ImagePath") var image: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}