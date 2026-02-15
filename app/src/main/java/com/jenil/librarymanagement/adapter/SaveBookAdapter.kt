package com.jenil.librarymanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jenil.librarymanagement.Data.SaveBook
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.databinding.ItemSaveBookBinding

class SaveBookAdapter(
    private val saveBookList: List<SaveBook>
): RecyclerView.Adapter<SaveBookAdapter.SaveBookViewHolder>() {

    inner class SaveBookViewHolder(private val binding: ItemSaveBookBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(saveBook: SaveBook){
            binding.apply {
                tvSaveBookName.text = saveBook.bookName
                tvSaveBookCategory.text = saveBook.categoryName
                tvSaveBookAuthorName.text = saveBook.bookAuthor

                Glide.with(binding.root)
                    .load(saveBook.url)
                    .placeholder(R.drawable.ic_place)
                    .into(ivSaveBookImg)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveBookViewHolder {
        return SaveBookViewHolder(ItemSaveBookBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return saveBookList.size
    }

    override fun onBindViewHolder(holder: SaveBookViewHolder, position: Int) {
        holder.bind(saveBookList[position])
    }
}