package com.jenil.librarymanagement.fragments.Admin.View.BookOperation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.jenil.librarymanagement.adapter.CategoryAdapter
import com.jenil.librarymanagement.databinding.FragmentCategoryAdminBinding

class CategoryAdminFragment : Fragment() {

    private lateinit var binding: FragmentCategoryAdminBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var myDB: DatabaseReference
    private var categoryList: MutableList<CategoryModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryAdminBinding.inflate(layoutInflater)

        binding.idAppBar.tvAppName.text = getString(R.string.category)

        binding.idAppBar.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.idRecyclerCategory.apply {
            layoutManager = GridLayoutManager(requireContext(), 2,GridLayoutManager.VERTICAL,false)
            adapter = CategoryAdapter(categoryList) {
                    categoryModel ->
                SharedPreference.putInt("categoryID", categoryModel.id.toInt())
                findNavController().navigate(R.id.action_categoryAdminFragment_to_bookListFragment)

            }
        }

        firebaseDatabase = FirebaseDatabase.getInstance()
        myDB = firebaseDatabase.getReference().child("Books")

        binding.idProgressBar.visibility = View.VISIBLE
        fetchCategory()
        binding.idProgressBar.visibility = View.GONE

        return binding.root
    }



    private fun fetchCategory() {

        myDB.addListenerForSingleValueEvent(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList.clear()

                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val imageUrl = data.child("url").getValue(String::class.java)
                        val name = data.child("category").getValue(String::class.java)

                        if (imageUrl != null && name != null){
                            val categoryModel = CategoryModel(name,imageUrl,data.key.toString())
                            categoryList.add(categoryModel)
                        }

                        if(categoryList.isEmpty()){
                            binding.idProgressBar.visibility = View.GONE
                            binding.tvNoFound.visibility = View.VISIBLE
                            binding.idRecyclerCategory.visibility = View.GONE
                        }else{
                            binding.idProgressBar.visibility = View.GONE
                            binding.tvNoFound.visibility = View.GONE
                            binding.idRecyclerCategory.visibility = View.VISIBLE
                        }
                    }
                }
                binding.idRecyclerCategory.adapter?.notifyDataSetChanged()

                binding.idProgressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {

                binding.idProgressBar.visibility = View.GONE
                Log.e("Error", error.message)
            }
        })

    }
}