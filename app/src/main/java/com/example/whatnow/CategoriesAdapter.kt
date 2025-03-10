package com.example.whatnow

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.whatnow.databinding.CategoryListItemBinding

class CategoriesAdapter(
    private val activity: Activity,
    private val categories: MutableList<Category>,
    private val onCategorySelected: (String) -> Unit,
) : RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {

    class CategoriesViewHolder(val binding: CategoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val b = CategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriesViewHolder(b)
    }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.categoryTv.text = categories[position].categoryTitle

        holder.binding.categoryImage.setImageResource(category.image)

        val url = categories[position].url
        holder.binding.categoryContainer.setOnClickListener {
            onCategorySelected(category.categoryTitle)
        }
    }

//    fun updateCategories(newCategories: List<Category>) {
//        categories.clear()
//        categories.addAll(newCategories)
//        notifyDataSetChanged()
//    }
    }
