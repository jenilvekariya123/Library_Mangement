package com.jenil.librarymanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jenil.librarymanagement.Data.BookInfo
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.databinding.ItemBookUserBinding

class NewArrivalAdapter(
    private val bookList : List<BookInfo>,
    private val onClick : (BookInfo) -> Unit
) : RecyclerView.Adapter<NewArrivalAdapter.NewArrivalViewHolder>() {
    inner class NewArrivalViewHolder(private val binding: ItemBookUserBinding) : RecyclerView.ViewHolder(binding.root) {

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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewArrivalAdapter.NewArrivalViewHolder {
        return NewArrivalViewHolder(ItemBookUserBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: NewArrivalAdapter.NewArrivalViewHolder, position: Int) {
        holder.bind(bookList[position])
    }

    override fun getItemCount(): Int {
        return 12
    }

}