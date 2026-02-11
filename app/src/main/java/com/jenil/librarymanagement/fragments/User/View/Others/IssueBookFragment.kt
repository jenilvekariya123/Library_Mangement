package com.jenil.librarymanagement.fragments.User.View.Others

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.jenil.librarymanagement.Data.IssueBook
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.adapter.IssueBookAdapter
import com.jenil.librarymanagement.databinding.FragmentIssueBookBinding

class IssueBookFragment : Fragment() {

    private lateinit var binding: FragmentIssueBookBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var adapter: IssueBookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIssueBookBinding.inflate(layoutInflater)

        adapter = IssueBookAdapter(mutableListOf())
        binding.idRecyclerIssueBook.adapter = adapter
        binding.idRecyclerIssueBook.layoutManager = LinearLayoutManager(requireContext())

        binding.idAppBar.apply {
            tvAppName.text = getString(R.string.issue_book)
            ivBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        val studID = SharedPreference.getString("StudentID", "")

        if (!studID.isNullOrEmpty()) {
            fetchAllData(studID)
        } else {
            Log.e("TAG", "Student ID is null or empty")
            displayEmptyState()
        }

        return binding.root
    }

    private fun fetchAllData(studID: String) {
        firebaseDatabase = FirebaseDatabase.getInstance()
        val bookRef = firebaseDatabase.getReference("Books")
        val userRef = firebaseDatabase.getReference("Users")
        val categoryRef = firebaseDatabase.getReference("Books")
        databaseReference = firebaseDatabase.getReference("IssueBook").child(studID)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val issueList = mutableListOf<IssueBook>()

                for (issueSnapshot in snapshot.children) {
                    val bookNo = issueSnapshot.child("bookNo").getValue(String::class.java) ?: ""
                    val categoryID = issueSnapshot.child("category").getValue(String::class.java) ?: ""
                    val issueDate = issueSnapshot.child("issueDate").getValue(String::class.java) ?: ""
                    val returnDate = issueSnapshot.child("returnDate").getValue(String::class.java) ?: "NA"
                    val studId = issueSnapshot.child("studID").getValue(String::class.java) ?: ""

                    Log.d("TAG", "Processing bookNo: $bookNo, categoryID: $categoryID for studID: $studId")

                    if (bookNo.isNotEmpty() && categoryID.isNotEmpty()) {
                        bookRef.child(categoryID).child("BookList").child(bookNo)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(bookSnapshot: DataSnapshot) {
                                    val bookName = bookSnapshot.child("name").getValue(String::class.java) ?: "Unknown"
                                    val imageUrl = bookSnapshot.child("url").getValue(String::class.java) ?: ""

                                    categoryRef.child(categoryID).addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(categorySnapshot: DataSnapshot) {
                                            val categoryName = categorySnapshot.child("category").getValue(String::class.java) ?: "Unknown"

                                            userRef.child(studId).addValueEventListener(object : ValueEventListener {
                                                override fun onDataChange(userSnapshot: DataSnapshot) {
                                                    val userName = userSnapshot.child("fullName").getValue(String::class.java) ?: "Unknown"

                                                    issueList.add(
                                                        IssueBook(
                                                            fullName = userName,
                                                            studId = studId,
                                                            bookNo = bookNo,
                                                            category = categoryID,
                                                            bookName = bookName,
                                                            imageUrl = imageUrl,
                                                            categoryName = categoryName,
                                                            issueDate = issueDate,
                                                            returnDate = returnDate
                                                        )
                                                    )

                                                    setUpRecycler(issueList)
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    Log.e("TAG", "Failed to fetch user details: ${error.message}")
                                                }
                                            })
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Log.e("TAG", "Failed to fetch category: ${error.message}")
                                        }
                                    })
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("TAG", "Failed to fetch book: ${error.message}")
                                }
                            })
                    } else {
                        Log.e("TAG", "bookNo or categoryID is empty")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "Failed to fetch issued books: ${error.message}")
                displayEmptyState()
            }
        })
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setUpRecycler(issueList: List<IssueBook>) {
        if (issueList.isEmpty()) {
            displayEmptyState()
        } else {
            binding.tvNoIssueBook.visibility = View.GONE
            binding.ivEmptyIssue.visibility = View.GONE
            binding.idRecyclerIssueBook.visibility = View.VISIBLE
            adapter.updateList(issueList)
        }
    }

    private fun displayEmptyState() {
        binding.tvNoIssueBook.visibility = View.VISIBLE
        binding.ivEmptyIssue.visibility = View.VISIBLE
        binding.idRecyclerIssueBook.visibility = View.GONE
    }
}
