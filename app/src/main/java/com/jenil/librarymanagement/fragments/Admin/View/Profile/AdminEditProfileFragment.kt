package com.jenil.librarymanagement.fragments.Admin.View.Profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.DialogUtils
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.databinding.FragmentAdminEditProfileBinding


class AdminEditProfileFragment : Fragment() {

    private lateinit var binding: FragmentAdminEditProfileBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminEditProfileBinding.inflate(layoutInflater)

        val orgNo = SharedPreference.getString("adminOrganizationNo","")

        binding.apply {
            idAppBar.ivBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
                idAppBar.tvAppName.text = getString(R.string.edit_profile)

        }

        setDataEditText(orgNo.toString())

        binding.btnSave.setOnClickListener {
            editProfile(orgNo.toString())
        }

        return binding.root
    }

    private fun setDataEditText(adminNo : String){
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference()

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val libraryName = snapshot.child("Admin").child(adminNo).child("LibraryName").getValue(String::class.java)
                val contactNo = snapshot.child("Admin").child(adminNo).child("LibraryPhone").getValue(String::class.java)

                if (libraryName != null){
                    binding.etLibraryName.setText(libraryName)
                }else{
                    binding.etLibraryName.setText(getString(R.string.not_available))
                }

                if (contactNo != null){
                    binding.etContactNo.setText(contactNo)
                }else{
                    binding.etContactNo.setText(getString(R.string.not_available))
                }

                if (snapshot.child("Admin").child(adminNo).child("Address").exists()){
                    val libraryAddress = snapshot.child("Admin").child(adminNo).child("Address").getValue(String::class.java)
                    binding.etAddress.setText(libraryAddress)
                }else{
                    binding.etAddress.setText(getString(R.string.not_available))
                }

                if (snapshot.child("Admin").child(adminNo).child("Email").exists()){
                    val libraryEmail = snapshot.child("Admin").child(adminNo).child("Email").getValue(String::class.java)
                    binding.etEmailAddress.setText(libraryEmail)
                }else{
                    binding.etEmailAddress.setText(getString(R.string.not_available))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Tag", "Error updating data: ${error.message}")
            }

        })
    }

    private fun editProfile(adminNo: String) {
        DialogUtils.loader(requireContext(), "Loading...")

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference().child("Admin")

        val updates = mutableMapOf<String, Any>()

        updates["LibraryName"] = binding.etLibraryName.text.toString()
        updates["LibraryPhone"] = binding.etContactNo.text.toString()

        if (binding.etAddress.text.toString().isNotEmpty()) {
            updates["Address"] = binding.etAddress.text.toString()
        }

        if (binding.etEmailAddress.text.toString().isNotEmpty()) {
            updates["Email"] = binding.etEmailAddress.text.toString()
        }

        databaseReference.child(adminNo).updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(),"Profile Updated Successfully",Toast.LENGTH_SHORT).show()
                DialogUtils.dismissDialog()
            } else {
                DialogUtils.dismissDialog()
                Log.e("Tag", "Error updating data: ${task.exception?.message}")
            }
        }
    }

}