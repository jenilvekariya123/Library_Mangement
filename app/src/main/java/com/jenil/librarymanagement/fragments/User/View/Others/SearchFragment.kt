package com.jenil.librarymanagement.fragments.User.View.Others

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.jenil.librarymanagement.Data.BookInfo
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.adapter.SearchAdapter
import com.jenil.librarymanagement.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var adpater : SearchAdapter
    private val searchList = mutableListOf<Pair<BookInfo, String>>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val studId = SharedPreference.getString("StudentID","")

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Books")



        binding.etSearchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("NotifyDataSetChanged")
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    searchView(query,studId)
                } else {
                    binding.tvNoBookFound.visibility = View.VISIBLE
                    binding.idRecyclerSearch.visibility = View.GONE
                    searchList.clear()
                    adpater.notifyDataSetChanged()
                }
            }
        })

        return binding.root
    }

    private fun searchView(query: String, studID: String?) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                searchList.clear()
                for (categorySnapshot in snapshot.children) {
                    val categoryId = categorySnapshot.key
                    Log.e("SearchFragment", "Category: $categoryId")
                    val bookRef = categorySnapshot.child("BookList")
                    for (bookSnapshot in bookRef.children) {
                        val bookInfo = bookSnapshot.getValue(BookInfo::class.java)
                        if (bookInfo != null && bookInfo.name!!.contains(query, ignoreCase = true)) {
                            // Add the bookInfo and categoryId as a pair
                            searchList.add(Pair(bookInfo, categoryId ?: ""))
                        }
                    }
                }
                if (searchList.isEmpty()) {
                    binding.tvNoBookFound.visibility = View.VISIBLE
                    binding.ivNotFound.visibility = View.VISIBLE
                    binding.idRecyclerSearch.visibility = View.GONE
                } else {
                    binding.tvNoBookFound.visibility = View.GONE
                    binding.ivNotFound.visibility = View.GONE
                    binding.idRecyclerSearch.visibility = View.VISIBLE

                    adpater = SearchAdapter(searchList) {
                                                        selectedBookInfo, selectedCategoryId ->
//                        Log.e("SearchFragment", "Selected Book ID: ${selectedBookInfo.id}")
//                        Log.e("SearchFragment", "Selected Category ID: $selectedCategoryId")
                        SharedPreference.putString("BookID", selectedBookInfo.id)
                        SharedPreference.putString("CategoryId", selectedCategoryId)
                        SharedPreference.putString("StudentID", studID)
                        findNavController().navigate(R.id.action_searchFragment_to_bookDetailsFragment)
                    }

                    binding.idRecyclerSearch.layoutManager = GridLayoutManager(requireContext(), 3)
                    binding.idRecyclerSearch.adapter = adpater
                    adpater.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SearchFragment", "Error: ${error.message}")
            }
        })
    }
}