package com.jenil.librarymanagement.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jenil.librarymanagement.Data.IssueBook
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.databinding.ItemIssueBookBinding

class AdminIssuedBookAdapter(
    private var issueBookList : List<IssueBook>,
    private val onClick : (IssueBook) -> Unit,
    private val onMoreClick : (IssueBook) -> Unit
):
    RecyclerView.Adapter<AdminIssuedBookAdapter.AdminIssuedBookViewHolder>() {

    inner class AdminIssuedBookViewHolder(private val binding: ItemIssueBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(issueBook: IssueBook) {
            binding.tvSaveBookName.text = issueBook.bookName
            binding.tvSaveBookAuthorName.text = issueBook.categoryName
            binding.tvIssueDate.text = issueBook.issueDate
            binding.tvReturnDateTitle.visibility = View.VISIBLE
            binding.tvReturnDate.visibility = View.VISIBLE
            binding.tvReturnDate.text = issueBook.returnDate
            binding.tvUserName.text = "Issued By ${issueBook.fullName}"

            Glide.with(binding.root).load(issueBook.imageUrl).placeholder(R.drawable.ic_place)
                .into(binding.ivIssueBookImg)

            binding.idLayoutIssuedBook.setOnClickListener {
                onClick(issueBook)
            }

            binding.ivMore.setOnClickListener {
                val popupMenu = PopupMenu(binding.root.context, binding.ivMore)
                popupMenu.inflate(R.menu.menu)
                popupMenu.setOnMenuItemClickListener {
                    item -> when(item.itemId){
                        R.id.idDelete -> {
                            onMoreClick(issueBook)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newIssueBookList: List<IssueBook>){
        issueBookList = newIssueBookList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminIssuedBookViewHolder {
        return AdminIssuedBookViewHolder(
            ItemIssueBookBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: AdminIssuedBookViewHolder,
        position: Int
    ) {
        holder.bind(issueBookList[position])
    }

    override fun getItemCount(): Int {
        return issueBookList.size
    }
}