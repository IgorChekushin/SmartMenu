package com.example.smartmenu.retrofit

data class ResponseIngredientsBody(val id: Int, val name: String, val recipe_Id : Int)

data class ResponseRecipeBody(val id: Int, val name: String, val description: String, val image_Urls : String)