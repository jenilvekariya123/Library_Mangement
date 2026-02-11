package com.jenil.librarymanagement.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jenil.librarymanagement.Data.userInfo
import com.jenil.librarymanagement.databinding.ItemUsersBinding

class UserDataAdapter(
    private val userList: List<userInfo>,
    private val onClick: (userInfo) -> Unit
) : RecyclerView.Adapter<UserDataAdapter.UserDataViewHolder>() {
    inner class UserDataViewHolder(private val binding: ItemUsersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userInfo: userInfo){
            binding.tvUserName.text = userInfo.fullName
            binding.tvStudId.text = userInfo.studId
            binding.tvMobile.text = userInfo.mobile

            binding.idCardViewUser.setOnClickListener {
                onClick(userInfo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDataViewHolder {
       return UserDataViewHolder(ItemUsersBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserDataViewHolder, position: Int) {
        holder.bind(userList[position])
    }
}