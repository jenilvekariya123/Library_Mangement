package com.jenil.librarymanagement.fragments.User.View.Others

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
import com.jenil.librarymanagement.Data.SaveBook
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.DialogUtils
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.adapter.SaveBookAdapter
import com.jenil.librarymanagement.databinding.FragmentSaveBookBinding

class SaveBookFragment : Fragment() {

    private lateinit var binding: FragmentSaveBookBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveBookBinding.inflate(inflater, container, false)

        val studID = SharedPreference.getString("StudentID", "")


        binding.idAppBar.apply {
            tvAppName.text = getString(R.string.save_book)
            ivBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        fetchSaveBookData(studID.toString())

        return binding.root
    }

    private fun fetchSaveBookData(studId: String) {
        DialogUtils.loader(requireContext(), "Loading...")

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference().child("Users").child(studId).child("SavedBook")
        val bookDataReference = firebaseDatabase.getReference().child("Books")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val savedList = mutableListOf<SaveBook>()

                for (saveSnapshot in snapshot.children) {
                    val bookNo = saveSnapshot.child("bookNo").getValue(String::class.java)
                    val categoryID = saveSnapshot.child("category").getValue(String::class.java)

                    //Fetch book data
                    if (bookNo != null && categoryID != null) {
                        bookDataReference.child(categoryID).child("BookList").child(bookNo)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(bookSnapshot: DataSnapshot) {
                                    val bookName = bookSnapshot.child("name").getValue(String::class.java)
                                    val bookAuthor = bookSnapshot.child("author").getValue(String::class.java)
                                    val bookUrl = bookSnapshot.child("url").getValue(String::class.java)

                                    // Fetch category name
                                    bookDataReference.child(categoryID).addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(categorySnapshot: DataSnapshot) {
                                            val categoryName = categorySnapshot.child("category").getValue(String::class.java)

                                            savedList.add(
                                                SaveBook(
                                                    bookNo,
                                                    bookName ?: "Unknown Book",
                                                    bookAuthor ?: "Unknown Author",
                                                    categoryName ?: "Unknown Category",
                                                    bookUrl ?: ""
                                                )
                                            )
                                            DialogUtils.dismissDialog()
                                            setUpRecycler(savedList)
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Log.e("TAG", "SaveFragment (category):::${error.message}")
                                                DialogUtils.dismissDialog()

                                        }
                                    })
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("TAG", "SaveFragment (book):::${error.message}")
                                    DialogUtils.dismissDialog()
                                }
                            })
                    } else {
                        DialogUtils.dismissDialog()
                    }
                }

                DialogUtils.dismissDialog()
            }

            override fun onCancelled(error: DatabaseError) {
                DialogUtils.dismissDialog()
                Log.e("TAG", "SaveFragment (saved book list):::${error.message}")
            }
        })
    }



    private fun setUpRecycler(saveList: List<SaveBook>) {
        if (saveList.isEmpty()) {
            binding.tvNoSaveBook.visibility = View.VISIBLE
            binding.ivEmptySave.visibility = View.VISIBLE
            binding.idRecyclerSaveBook.visibility = View.GONE
        } else {
            binding.idRecyclerSaveBook.visibility = View.VISIBLE
            binding.tvNoSaveBook.visibility = View.GONE
            binding.ivEmptySave.visibility = View.GONE
            val adapter = SaveBookAdapter(saveList)
            binding.idRecyclerSaveBook.adapter = adapter
            binding.idRecyclerSaveBook.layoutManager = LinearLayoutManager(requireContext())
        }
    }





}
