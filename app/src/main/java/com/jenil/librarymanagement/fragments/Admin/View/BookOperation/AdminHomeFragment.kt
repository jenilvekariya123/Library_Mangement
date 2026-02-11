package com.jenil.librarymanagement.fragments.Admin.View.BookOperation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.DialogUtils
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.activities.MainActivity
import com.jenil.librarymanagement.databinding.FragmentAdminHomeBinding


class AdminHomeFragment : Fragment() {

    private lateinit var binding: FragmentAdminHomeBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentAdminHomeBinding.inflate(layoutInflater)


        val libraryNo = SharedPreference.getString("adminOrganizationNo",null)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference().child("Admin")

        setupHeaderData(libraryNo)
        setupName(libraryNo)
        setTotalBook()
        setTotalIssueBook()
        setTotalUser()

        binding.idAddBook.setOnClickListener {
            findNavController().navigate(R.id.action_adminHomeFragment_to_addBookFragment)
        }

        binding.idUpdateBook.setOnClickListener {
            findNavController().navigate(R.id.action_adminHomeFragment_to_categoryAdminFragment)

        }
        binding.idDeleteBook.setOnClickListener {
            findNavController().navigate(R.id.action_adminHomeFragment_to_categoryAdminFragment)
        }

        binding.ivMenu.setOnClickListener {
            openDrawer(binding.idAdminDrawerLayout)
        }

        binding.idTotalUser.setOnClickListener {
            findNavController().navigate(R.id.action_adminHomeFragment_to_userDetailsFragment)

        }
        setDrawerData()

        return binding.root
    }

    private fun openDrawer(drawerLayout: DrawerLayout){
        drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun setDrawerData(){
        binding.idDrawerAdmin.apply {
            home.setOnClickListener{
                closeDrawer(binding.idAdminDrawerLayout)
            }
            Profile.setOnClickListener {
                findNavController().navigate(R.id.action_adminHomeFragment_to_adminProfileFragment)
            }
            AddBook.setOnClickListener {
                findNavController().navigate(R.id.action_adminHomeFragment_to_addBookFragment)
            }
            UpdateBook.setOnClickListener {
                findNavController().navigate(R.id.action_adminHomeFragment_to_categoryAdminFragment)
            }
            delete.setOnClickListener {
                findNavController().navigate(R.id.action_adminHomeFragment_to_categoryAdminFragment)
            }
            setting.setOnClickListener {
                findNavController().navigate(R.id.action_adminHomeFragment_to_settingFragment)
            }
            logout.setOnClickListener {
                DialogUtils.showDialog(requireActivity(),
                    "Are you sure you want to logout?","Log Out","Cancel",
                    object : DialogUtils.OnDialogButtonClick{
                        override fun onPositiveButtonClick() {
                            SharedPreference.clearSharedPreference()
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        }

                        override fun onNegativeButtonClick() {
                            DialogUtils.dismissDialog()
                        }
                    })
            }
        }
    }

    private fun closeDrawer(drawerLayout: DrawerLayout){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun setupHeaderData(libraryNo: String?) {
        binding.headerAdmin.apply {
            if (libraryNo != null) {
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (isAdded && snapshot.hasChild(libraryNo)) {
                            val libraryName = snapshot.child(libraryNo)
                                .child("LibraryName")
                                .getValue(String::class.java)
                            tvLibraryId.text = getString(R.string.library_no, libraryNo)
                            tvLibraryName.text = libraryName
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
            }
        }
    }


    private fun setupName(libraryNo: String?) {

            if (libraryNo != null){
                databaseReference.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild(libraryNo)){
                            val libraryName = snapshot.child(libraryNo).child("LibraryName").getValue(String::class.java)
                            binding.tvLibraryName.text = libraryName
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {  }
                })
            }

    }

    private fun setTotalBook() {
        val myRef = FirebaseDatabase.getInstance().getReference().child("Books")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var total = 0
                for (child in snapshot.children) {
                    val temp = child.child("BookList").childrenCount.toInt()
                    total += temp
                }
                binding.tvCountBook.text = total.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "Error: ${error.message}")
            }
        })

    }

    private fun setTotalUser() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference().child("Users")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val child: Int = snapshot.childrenCount.toInt()
                Log.d("Total Users", child.toString())
                binding.tvCountUser.text = child.toString()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setTotalIssueBook() {
        val ref = FirebaseDatabase.getInstance().getReference().child("IssueBook")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalIssuedBooks = 0
                for (studentSnapshot in snapshot.children) {
                    totalIssuedBooks += studentSnapshot.childrenCount.toInt()

                    val issuedID = studentSnapshot.key
                    Log.e("TAG", "Total Issued Books: $issuedID")

                    binding.idTotalIssueBook.setOnClickListener {
                        findNavController().navigate(R.id.action_adminHomeFragment_to_issueBookRequestFragment)
                        SharedPreference.putString("issuedStudentID",issuedID.toString())
                    }
                }

                Log.e("TAG", "Total Issued Books: $totalIssuedBooks")
                binding.tvCountIssueBook.text = totalIssuedBooks.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "Failed to fetch issued books count: ${error.message}")
            }
        })
    }


    override fun onPause() {
        super.onPause()
        closeDrawer(binding.idAdminDrawerLayout)
    }

}