package com.jenil.librarymanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jenil.librarymanagement.Data.CategoryModel
import com.jenil.librarymanagement.databinding.ItemCategoryBinding

class HomeCategoryAdapter(
    private val categoryList: List<CategoryModel>,
    private val onClick : (CategoryModel) -> Unit
) : RecyclerView.Adapter<HomeCategoryAdapter.HomeCategoryHolder>() {

    inner class HomeCategoryHolder(private val binding: ItemCategoryBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(category : CategoryModel){
            binding.tvCategoryName.text = category.name
            Glide.with(binding.root).load(category.imageUrl).into(binding.ivCategoryImg)

            binding.idCategoryLayout.setOnClickListener {
                onClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoryAdapter.HomeCategoryHolder {
        return HomeCategoryHolder(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: HomeCategoryAdapter.HomeCategoryHolder, position: Int) {
        holder.bind(categoryList[position])
    }
}