package com.jenil.librarymanagement.fragments.Admin.Start

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.jenil.librarymanagement.activities.AdminHomeActivity
import com.jenil.librarymanagement.databinding.FragmentAdminLoginBinding


class AdminLoginFragment : Fragment() {

    private lateinit var binding: FragmentAdminLoginBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminLoginBinding.inflate(layoutInflater)

        binding.tvForgetPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_adminRegisterFragment)
        }

        val adminOrganizationNo = binding.etOrganization
        val pass = binding.etPassword

        binding.btnLogin.setOnClickListener {
            if (checkAllField(adminOrganizationNo, pass)) {

                adminLogin(adminOrganizationNo, pass)
            }
        }

        return binding.root
    }

    private fun checkAllField(adminOrganizationNo: EditText, password: EditText): Boolean {
        var valid = true
        if (adminOrganizationNo.text.toString().isEmpty()) {
            Toast.makeText(requireContext(),"Enter Organization Number", Toast.LENGTH_SHORT).show()
            adminOrganizationNo.requestFocus()
            valid = false
        }
        if (password.text.toString().isEmpty()) {
            Toast.makeText(requireContext(),"Enter Password", Toast.LENGTH_SHORT).show()
            password.requestFocus()
            valid = false
        }
        return valid
    }

    private fun adminLogin(adminOrganizationNo: EditText, password: EditText) {

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference().child("Admin")

        DialogUtils.loader(requireContext(),"Loading...")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                DialogUtils.dismissDialog()
                if (snapshot.hasChild(adminOrganizationNo.text.toString())) {
                    val passData = snapshot.child(adminOrganizationNo.text.toString()).child("Password").getValue(String::class.java)

                    if (password.text.toString() == passData) {

                        Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()

                        val intent = Intent(requireContext(), AdminHomeActivity::class.java)
                        intent.putExtra("adminOrganizationNo", adminOrganizationNo.text.toString())
                        SharedPreference.putString("adminOrganizationNo", adminOrganizationNo.text.toString())
                        SharedPreference.putBoolean("isAdminLogin",true)
                        startActivity(intent)
                    } else {
                        Toast.makeText(requireContext(), "Incorrect Number or Password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Organization number is not registered with us.", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                DialogUtils.dismissDialog()
                Log.e("DatabaseError", "Error: ${error.message}")
            }
        })
    }
}
