package com.jenil.librarymanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jenil.librarymanagement.Data.BookInfo
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.databinding.ItemBookUserBinding

class HomeBooksAdapter(private val bookList : List<BookInfo>,
                       private val onClick : (BookInfo) -> Unit):
    RecyclerView.Adapter<HomeBooksAdapter.HomeBooksViewHolder>() {
    inner class HomeBooksViewHolder(private val binding: ItemBookUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: BookInfo) {
            binding.tvBookName.text = book.name

            Glide.with(binding.root)
                .load(book.url)
                .placeholder(R.drawable.ic_place)
                .into(binding.ivBookImg)

            binding.idCard.setOnClickListener {
                onClick(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBooksViewHolder {
        return HomeBooksViewHolder(ItemBookUserBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
       return bookList.size
    }

    override fun onBindViewHolder(holder: HomeBooksViewHolder, position: Int) {
        holder.bind(bookList[position])
    }
}