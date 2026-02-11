package com.jenil.librarymanagement.fragments.Admin.View.Others

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jenil.librarymanagement.Data.IssueBook
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.adapter.AdminIssuedBookAdapter
import com.jenil.librarymanagement.databinding.FragmentIssueBookRequestBinding


class IssueBookRequestFragment : Fragment() {

    private lateinit var binding: FragmentIssueBookRequestBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: AdminIssuedBookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIssueBookRequestBinding.inflate(layoutInflater)



//        val studId = SharedPreference.getString("issuedStudentID","")

        binding.idAppBar.apply {
            tvAppName.text = getString(R.string.issue_book_request)
            ivBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

//        fetchAllData(studId.toString())
        fetchAllData()

        return binding.root
    }
    private fun fetchAllData() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("IssueBook")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val issueList = mutableListOf<IssueBook>()

                for (issueSnapshot in snapshot.children) {
                    val studId = issueSnapshot.key!!.toString()
                    for (childSnapshot in issueSnapshot.children) {
                        val issuedId: Int = childSnapshot.key!!.toInt()
                        val bookNo = childSnapshot.child("bookNo").getValue(String::class.java) ?: ""
                        val categoryID = childSnapshot.child("category").getValue(String::class.java) ?: ""
                        val issueDate = childSnapshot.child("issueDate").getValue(String::class.java) ?: ""
                        val returnDate = childSnapshot.child("returnDate").getValue(String::class.java) ?: "NA"

                        val bookRef = firebaseDatabase.getReference("Books").child(categoryID).child("BookList").child(bookNo)
                        bookRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(bookSnapshot: DataSnapshot) {
                                val bookName = bookSnapshot.child("name").getValue(String::class.java) ?: "Unknown"
                                val imageUrl = bookSnapshot.child("url").getValue(String::class.java) ?: ""

                                val categoryRef = firebaseDatabase.getReference("Books").child(categoryID)
                                categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(categorySnapshot: DataSnapshot) {
                                        val categoryName = categorySnapshot.child("category").getValue(String::class.java) ?: "Unknown"

                                        val userRef = firebaseDatabase.getReference("Users").child(studId)
                                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
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

                                                setUpRecycler(issueList, issuedId)
                                            }

                                            override fun onCancelled(error: DatabaseError) { Log.e("TAG", "Failed to fetch user details: ${error.message}") }
                                        })
                                    }
                                    override fun onCancelled(error: DatabaseError) { Log.e("TAG", "Failed to fetch category: ${error.message}") }
                                })
                            }
                            override fun onCancelled(error: DatabaseError) { Log.e("TAG", "Failed to fetch book: ${error.message}") }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "Failed to fetch issued books: ${error.message}")
                displayEmptyState() }
        })
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setUpRecycler(issueList: List<IssueBook>, issuedId: Int) {
        if (issueList.isEmpty()) {
            displayEmptyState()
        } else {
            binding.idRecyclerIssueBook.visibility = View.VISIBLE
            adapter = AdminIssuedBookAdapter(issueList ,{
                SharedPreference.putInt("issuedID", issuedId)
                SharedPreference.putString("issuedStudentID", it.studId)
                SharedPreference.putString("issuedBookID", it.bookNo)
                SharedPreference.putString("issuedCategoryID", it.category)
                SharedPreference.putString("issueDate", it.issueDate)
                SharedPreference.putString("returnDate", it.returnDate)
                Log.e("TAG", "setUpRecycler: This node :: $issuedId,${it.bookNo},${it.category}")
                findNavController().navigate(R.id.action_issueBookRequestFragment_to_issuedBookFragment)
            },{
                databaseReference = FirebaseDatabase.getInstance().getReference().child("IssueBook")
                databaseReference.child(it.studId).child(issuedId.toString()).removeValue()
            })
            binding.idRecyclerIssueBook.adapter = adapter
            binding.idRecyclerIssueBook.layoutManager = LinearLayoutManager(requireContext())
            adapter.updateList(issueList)
        }
    }


    private fun displayEmptyState() {
        binding.idRecyclerIssueBook.visibility = View.GONE
    }
}

