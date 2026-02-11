package com.jenil.librarymanagement.fragments.Admin.View.Others

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.DialogUtils
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.databinding.FragmentIssuedBookBinding
import java.util.Calendar


class IssuedBookFragment : Fragment() {

    private lateinit var binding: FragmentIssuedBookBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIssuedBookBinding.inflate(layoutInflater)

        binding.idAppBar.apply {
            tvAppName.text = getString(R.string.issue_book)
            ivBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        val issuedBookID = SharedPreference.getInt("issuedID",0)
        val studID = SharedPreference.getString("issuedStudentID","")
        val bookId = SharedPreference.getString("issuedBookID","")
        val categoryId = SharedPreference.getString("issuedCategoryID","")
        val issueDate = SharedPreference.getString("issueDate","")
        val returnDate = SharedPreference.getString("returnDate","") ?: "NA"
        Log.e("TAG", "onCreateView: $studID,$bookId,$categoryId")

        setupUserData(studID.toString(),bookId.toString(),categoryId.toString())
        binding.etIssueDate.setText(issueDate)
        binding.etReturnDate.setText(returnDate)

        setupReturnDatePicker()

        binding.btnIssuedBook.setOnClickListener {
            if (validation()){
                DialogUtils.loader(requireContext(),"Loading...")
                enterData(studID.toString(),bookId.toString(),categoryId.toString(),issuedBookID)
                DialogUtils.dismissDialog()
                findNavController().navigate(R.id.action_issuedBookFragment_to_issueBookRequestFragment)
            }
        }

        return binding.root
    }

    private fun setupReturnDatePicker() {
        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.etReturnDate.setOnClickListener {
            @Suppress("DEPRECATION")
            DatePickerDialog(requireContext(),
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, { _, selectedYear, selectedMonth, dayOfMonth ->
                binding.etReturnDate.setText(String.format("$dayOfMonth/${selectedMonth + 1}/$selectedYear"))
            }, year, month, day).show()
        }
    }

    private fun setupUserData(studID: String,bookId: String,categoryId: String) {

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference().child("Users").child(studID)

        val bookRef = firebaseDatabase.getReference().child("Books").child(categoryId).child("BookList").child(bookId)


        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.apply {
                    etFullName.setText(snapshot.child("fullName").getValue(String::class.java))
                    etEmail.setText(snapshot.child("email").getValue(String::class.java))
                    etMobileNo.setText(snapshot.child("mobile").getValue(String::class.java))
                    etStudId.setText(snapshot.child("studId").getValue(String::class.java))

                }
                bookRef.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        binding.apply {
                            etBookName.setText(snapshot.child("name").getValue(String::class.java))
                            etAuthorName.setText(snapshot.child("author").getValue(String::class.java))
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("TAG", "onCancelled: :: ${error.message}")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "onCancelled: :: ${error.message}")
            }

        })
    }

    private fun enterData(studID: String,bookId: String,categoryId: String,issuedID: Int) {
        databaseReference = FirebaseDatabase.getInstance().getReference("IssueBook").child(studID).child(issuedID.toString())

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                   databaseReference.child("bookNo").setValue(bookId)
                   databaseReference.child("category").setValue(categoryId)
                   databaseReference.child("issueDate").setValue(binding.etIssueDate.text.toString())
                   databaseReference.child("returnDate").setValue(binding.etReturnDate.text.toString())
                   databaseReference.child("studID").setValue(studID)

                Toast.makeText(requireContext(),"Book Issued Successfully",Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun validation(): Boolean {

        var count = 0

        if (binding.etFullName.text.toString().isEmpty()){
            Toast.makeText(requireContext(),"This field is required",Toast.LENGTH_SHORT).show()
            count++
        }
        if (binding.etEmail.text.toString().isEmpty()){
            Toast.makeText(requireContext(),"This field is required",Toast.LENGTH_SHORT).show()
            count++
        }
        if (binding.etMobileNo.text.toString().isEmpty()){
            Toast.makeText(requireContext(),"This field is required",Toast.LENGTH_SHORT).show()
            count++
        }
        if (binding.etStudId.text.toString().isEmpty()){
            Toast.makeText(requireContext(),"This field is required",Toast.LENGTH_SHORT).show()
            count++
        }
        if (binding.etBookName.text.toString().isEmpty()){
            Toast.makeText(requireContext(),"This field is required",Toast.LENGTH_SHORT).show()
            count++
        }
        if (binding.etAuthorName.text.toString().isEmpty()){
            Toast.makeText(requireContext(),"This field is required",Toast.LENGTH_SHORT).show()
            count++
        }
        if (binding.etIssueDate.text.toString().isEmpty()){
            Toast.makeText(requireContext(),"This field is required",Toast.LENGTH_SHORT).show()
            count++
        }
        if (binding.etReturnDate.text.toString().isEmpty()){
            Toast.makeText(requireContext(),"This field is required",Toast.LENGTH_SHORT).show()
            count++
        }

        return count == 0
    }





}