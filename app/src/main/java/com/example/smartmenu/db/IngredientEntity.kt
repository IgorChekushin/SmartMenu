package com.example.smartmenu.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients_items")
data class IngredientEntity(
    @ColumnInfo(name = "ingredient") var ingredients: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}