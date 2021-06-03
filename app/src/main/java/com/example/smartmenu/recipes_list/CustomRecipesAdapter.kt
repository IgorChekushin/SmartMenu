package com.example.smartmenu.recipes_list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartmenu.R
import com.squareup.picasso.Picasso

class CustomRecipesAdapter(private var context: Context, private val listOfRecipes: List<Recipe>) :
    RecyclerView.Adapter<CustomRecipesAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeName: TextView = itemView.findViewById(R.id.dishName)
        val recipeDescription: TextView = itemView.findViewById(R.id.dishDesc)
        val recipeImage: ImageView = itemView.findViewById(R.id.dishImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.custom_recycler_view, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = listOfRecipes[position]
        holder.recipeName.text = recipe.name
        holder.recipeDescription.text = recipe.description
        Picasso.with(context).load(recipe.image).into(holder.recipeImage)
    }

    override fun getItemCount() = listOfRecipes.size
}