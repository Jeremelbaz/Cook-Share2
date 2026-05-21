package com.example.cookshare.view.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cookshare.databinding.ItemMealBinding
import com.example.cookshare.network.Meal
import com.squareup.picasso.Picasso

class MealAdapter(
    private var meals: List<Meal>,
    private val onMealClick: (Meal) -> Unit
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    inner class MealViewHolder(val binding: ItemMealBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]
        holder.binding.tvMealName.text = meal.strMeal
        holder.binding.tvMealCategory.text = meal.strCategory

        if (meal.strMealThumb.isNotEmpty()) {
            Picasso.get().load(meal.strMealThumb).into(holder.binding.imgMeal)
        }

        holder.binding.root.setOnClickListener {
            onMealClick(meal)
        }
    }

    override fun getItemCount() = meals.size

    fun updateMeals(newMeals: List<Meal>) {
        meals = newMeals
        notifyDataSetChanged()
    }
}