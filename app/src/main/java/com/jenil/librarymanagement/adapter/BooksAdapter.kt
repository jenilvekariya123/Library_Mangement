package com.jenil.librarymanagement.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jenil.librarymanagement.Data.BookInfo
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.databinding.ItemBooksBinding

class BooksAdapter(
    private val bookList: MutableList<BookInfo>,
    private val categoryId: Int,
    private val onUpdateClick: (String, Int) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<BooksAdapter.BookViewHolder>() {

    inner class BookViewHolder(private val binding: ItemBooksBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bookInfo: BookInfo) {
            binding.tvBookName.text = bookInfo.name

            Glide.with(binding.root).load(bookInfo.url).placeholder(R.drawable.ic_place).into(binding.ivBookImg)

            binding.btnEdit.setOnClickListener {
                onUpdateClick(bookInfo.id.toString(), categoryId)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(bookInfo.id.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(ItemBooksBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(bookList[position])
    }

    override fun getItemCount(): Int = bookList.size
}
