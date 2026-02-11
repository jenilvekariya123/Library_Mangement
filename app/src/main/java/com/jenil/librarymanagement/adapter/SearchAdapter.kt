package com.jenil.librarymanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jenil.librarymanagement.Data.BookInfo
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.databinding.ItemBookUserBinding

class SearchAdapter(
    private var dataList: List<Pair<BookInfo, String>>, // Change to List<Pair<BookInfo, String>>
    private val onItemClick: (BookInfo, String) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    inner class SearchViewHolder(private val binding: ItemBookUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bookInfo: BookInfo, categoryId: String) {
            binding.tvBookName.text = bookInfo.name
            Glide.with(binding.root.context).load(bookInfo.url).placeholder(R.drawable.ic_place).into(binding.ivBookImg)

            binding.idCard.setOnClickListener {
                onItemClick(bookInfo, categoryId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(ItemBookUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        // Restructure the pair into bookInfo and categoryId
        val (bookInfo, categoryId) = dataList[position]
        holder.bind(bookInfo, categoryId)
    }
}
