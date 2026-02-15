package com.jenil.librarymanagement.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jenil.librarymanagement.Data.IssueBook
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.databinding.ItemIssueBookBinding

class IssueBookAdapter (
    private var issueBookList: List<IssueBook>
): RecyclerView.Adapter<IssueBookAdapter.IssueBookViewHolder>() {
    inner class IssueBookViewHolder(private val binding: ItemIssueBookBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(issueBook: IssueBook) {
            binding.tvSaveBookName.text = issueBook.bookName
            binding.tvSaveBookAuthorName.text = issueBook.categoryName
            binding.tvIssueDate.text = issueBook.issueDate
            binding.tvReturnDate.text = issueBook.returnDate
            binding.tvUserName.text = "Issued By ${issueBook.fullName}"
            
            binding.ivMore.visibility = View.GONE

            Glide.with(binding.root).load(issueBook.imageUrl).placeholder(R.drawable.ic_place).into(binding.ivIssueBookImg)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<IssueBook>) {
        issueBookList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IssueBookViewHolder {
        return IssueBookViewHolder(ItemIssueBookBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: IssueBookViewHolder, position: Int) {
        holder.bind(issueBookList[position])
    }

    override fun getItemCount(): Int {
        return issueBookList.size
    }
}