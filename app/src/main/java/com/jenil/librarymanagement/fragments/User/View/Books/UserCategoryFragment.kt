package com.jenil.librarymanagement.fragments.User.View.Books

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jenil.librarymanagement.Data.CategoryModel
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.adapter.HomeCategoryAdapter
import com.jenil.librarymanagement.databinding.FragmentUserCategoryBinding


class UserCategoryFragment : Fragment() {

    private lateinit var binding : FragmentUserCategoryBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserCategoryBinding.inflate(layoutInflater)

        binding.idAppBar.apply {
            tvAppName.text = getString(R.string.category)
            ivBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        setCategoryData()

        return binding.root
    }

    private fun setCategoryData() {
        val categoryList = mutableListOf<CategoryModel>()

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Books")
        firebaseDatabase = FirebaseDatabase.getInstance()

        binding.idProgressBar.visibility = View.VISIBLE
        binding.idRecyclerCategoryList.visibility = View.GONE
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (categorySnapshot in snapshot.children){
                    val category = CategoryModel(
                        id = categorySnapshot.key.toString(),
                        name = categorySnapshot.child("category").getValue(String::class.java).toString(),
                        imageUrl = categorySnapshot.child("url").getValue(String::class.java).toString(),
                    )
                    categoryList.add(category)
                }
                setRecyclerAdapter(categoryList)
            }


            override fun onCancelled(error: DatabaseError) {
                binding.idProgressBar.visibility = View.GONE
                binding.idRecyclerCategoryList.visibility = View.VISIBLE
                TODO("Not yet implemented")
            }

        })
    }

    private fun setRecyclerAdapter (categoryList : MutableList<CategoryModel>) {
        val adapter = HomeCategoryAdapter(categoryList){ selectedCategory ->

            SharedPreference.putString("CategoryId",selectedCategory.id)
            findNavController().navigate(R.id.action_userCategoryFragment_to_userBookListFragment)
        }
        binding.idRecyclerCategoryList.apply {
            setAdapter(adapter)
            layoutManager = GridLayoutManager(requireContext(),2)
            visibility = View.VISIBLE
        }
        binding.idProgressBar.visibility = View.GONE
    }


}