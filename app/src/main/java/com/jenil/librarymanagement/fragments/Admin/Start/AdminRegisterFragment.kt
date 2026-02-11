package com.jenil.librarymanagement.fragments.Admin.Start

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.databinding.FragmentAdminRegisterBinding

class AdminRegisterFragment : Fragment() {

    private lateinit var binding: FragmentAdminRegisterBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminRegisterBinding.inflate(layoutInflater)


        val libraryName = binding.etLibraryName
        val organizationNO = binding.etOrganizationNo
        val phone = binding.etPhone
        val password = binding.etPassword

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_adminRegisterFragment_to_loginFragment)
        }

        binding.btnSignUp.setOnClickListener {
            if(checkAllField(libraryName, organizationNO, phone, password)){

                binding.idProgressBar.visibility = View.VISIBLE
                binding.tvBtnName.visibility = View.GONE
                adminRegister(libraryName, organizationNO, phone, password)
            }
        }

        return binding.root
    }

    private fun checkAllField(
        libraryName: EditText, organizationNO: EditText, phone: EditText, password: EditText
    ): Boolean {
        var valid = true
        if (libraryName.text.toString().isEmpty()) {
            libraryName.error = "Enter Full Name"
            libraryName.requestFocus()
            valid = false
        }
        if (organizationNO.text.toString().isEmpty()) {
            organizationNO.error = "Enter Organization No."
            organizationNO.requestFocus()
            valid = false
        }
        if (phone.text.toString().isEmpty()) {
            phone.error = "Enter Contact Number"
            phone.requestFocus()
            valid = false
        }
        if (password.text.toString().isEmpty()) {
            password.error = "Enter password"
            password.requestFocus()
            valid = false
        }

        return valid
    }

    private fun adminRegister(
        libraryName: EditText, organizationNO: EditText, phone: EditText, password: EditText
    ) {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference()

        if (checkAllField(libraryName, organizationNO, phone, password)) {
            if (password.text.length > 6) {
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (isAdded) {
                            if (snapshot.hasChild(organizationNO.text.toString())) {
                                Toast.makeText(requireContext(), "Admin Already Exists", Toast.LENGTH_SHORT).show()
                            } else {
                                val adminRef = databaseReference.child("Admin").child(organizationNO.text.toString())
                                adminRef.child("LibraryName").setValue(libraryName.text.toString())
                                adminRef.child("LibraryNumber").setValue(organizationNO.text.toString())
                                adminRef.child("LibraryPhone").setValue(phone.text.toString())
                                adminRef.child("Password").setValue(password.text.toString())

                                Toast.makeText(requireContext(), "Register successfully", Toast.LENGTH_SHORT).show()
                            }
                            binding.idProgressBar.visibility = View.GONE
                            binding.tvBtnName.visibility = View.VISIBLE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        if (isAdded) {
                            binding.idProgressBar.visibility = View.GONE
                            binding.tvBtnName.visibility = View.VISIBLE
                            Log.e("DatabaseError", "Error: ${error.message}")
                            Toast.makeText(requireContext(), "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } else {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Password must be greater than 6", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
