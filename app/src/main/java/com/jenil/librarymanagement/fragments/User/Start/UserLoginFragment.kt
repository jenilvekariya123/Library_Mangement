package com.jenil.librarymanagement.fragments.User.Start

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
import com.jenil.librarymanagement.Data.userInfo
import com.jenil.librarymanagement.activities.HomeActivity
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.DialogUtils
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.databinding.FragmentUserLoginBinding


class UserLoginFragment : Fragment() {

    private lateinit var binding: FragmentUserLoginBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserLoginBinding.inflate(inflater, container, false)

        val studId = binding.etStudId
        val pass = binding.etPassword

        binding.tvForgetPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_userRegisterFragment)
        }

        binding.btnLogin.setOnClickListener {
            if (checkAllField(studId, pass)) {
                usersLogin(studId, pass)
                clearFields(studId,pass)
            }
        }

        return binding.root
    }

    private fun clearFields(studId: EditText, password: EditText) {
        studId.setText("")
        password.setText("")
    }

    private fun checkAllField(studId: EditText, password: EditText): Boolean {
        var valid = true
        if (studId.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), "Enter Student ID", Toast.LENGTH_SHORT).show()
            studId.requestFocus()
            valid = false
        }
        if (password.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), "Enter Password", Toast.LENGTH_SHORT).show()
            password.requestFocus()
            valid = false
        }
        return valid
    }

    private fun usersLogin(studId: EditText, password: EditText) {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Users")

        DialogUtils.loader(requireContext(), "Loading...")

        val studentIdValue = studId.text.toString().trim()
        val passwordValue = password.text.toString().trim()

        if (studentIdValue.isEmpty() || passwordValue.isEmpty()) {
            DialogUtils.dismissDialog()
            Toast.makeText(requireContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show()
            return
        }

        databaseReference.child(studentIdValue).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                DialogUtils.dismissDialog()

                if (snapshot.exists()) {
                    Log.e("TAG", "onDataChange: DataSnapshot exists for studentId: $studentIdValue")
                    val userInfo = snapshot.getValue(userInfo::class.java)

                    Log.e("TAG", "Retrieved userInfo: $userInfo")

                    if (userInfo != null && userInfo.password == passwordValue) {
                        Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                        SharedPreference.putString("StudentID", studentIdValue)
                        SharedPreference.putBoolean("isUserLogin", true)
                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                    } else {
                        Toast.makeText(requireContext(), "Incorrect Student ID or Password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Student ID is not registered with us.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                DialogUtils.dismissDialog()
                Log.e("DatabaseError", "Error: ${error.message}")
                Toast.makeText(requireContext(), "Error fetching data. Please try again later.", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
