package com.jenil.librarymanagement.fragments.Admin.View.Others

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jenil.librarymanagement.Data.userInfo
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.adapter.UserDataAdapter
import com.jenil.librarymanagement.databinding.FragmentUserDetailsBinding


class UserDetailsFragment : Fragment() {

    private lateinit var binding: FragmentUserDetailsBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: UserDataAdapter
    private val userList = mutableListOf<userInfo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserDetailsBinding.inflate(layoutInflater)
        
        binding.idAppBar.apply { 
            tvAppName.text = getString(R.string.users_details)

            ivBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        fetchUserData()

        return binding.root
    }

    private fun fetchUserData() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference().child("Users")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (userSnapshot in snapshot.children){
                    val user = userSnapshot.getValue(userInfo::class.java)
                    user?.let { userList.add(it) }
                }
                setupRecycler(userList)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG","Error::: ${error.message}")
            }

        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecycler(userList: List<userInfo>){
        adapter = UserDataAdapter(userList) {
            deleteUser(it.studId)
        }
        binding.idRecyclerUserInfo.adapter = adapter
        binding.idRecyclerUserInfo.layoutManager = LinearLayoutManager(requireContext())
        binding.idRecyclerUserInfo.setHasFixedSize(true)
        adapter.notifyDataSetChanged()
    }

    private fun deleteUser(studId: String) {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference()
        databaseReference.child("Users").child(studId).removeValue()
    }


}