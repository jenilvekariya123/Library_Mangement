package com.jenil.librarymanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jenil.librarymanagement.Data.CategoryModel
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.databinding.ItemCategoryBinding

class CategoryAdapter(
    private var categoryList: MutableList<CategoryModel>,
    private var onItemClick: (CategoryModel) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root){

            fun bind(categoryModel: CategoryModel){

                binding.tvCategoryName.text = categoryModel.name
                Glide.with(binding.root.context)
                    .load(categoryModel.imageUrl)
                    .into(binding.ivCategoryImg)

                binding.idCategoryLayout.setOnClickListener {
                    onItemClick(categoryModel)
                }
            }

        }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAdapter.CategoryViewHolder {
        return CategoryViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryAdapter.CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}