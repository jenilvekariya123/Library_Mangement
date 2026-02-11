package com.jenil.librarymanagement.fragments.Admin.View.BookOperation

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.ImageDecoder
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.DialogUtils
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.databinding.FragmentUpdateBookBinding
import java.util.UUID

class UpdateBookFragment : Fragment() {

    private lateinit var binding: FragmentUpdateBookBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var filePath: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateBookBinding.inflate(layoutInflater)
        initFirebase()
        setupDatePicker()
        setupListeners()


        val categoryId = SharedPreference.getInt("categoryID", 0)
        val bookId: String = SharedPreference.getString("bookID", "0").toString()

        setDataEdittext(categoryId, bookId)

        binding.btnUpdateBook.setOnClickListener {
            if (isValidInput()) {
                if (binding.idSpinnerCategory.selectedItemPosition == categoryId) {
                    binding.idSpinnerCategory.setEnabled(false)
                    binding.btnUpdateBook.setEnabled(false)
                    DialogUtils.loader(requireContext(),"Uploading...")

                    updateBook(bookId)

                    DialogUtils.dismissDialog()
                    binding.btnUpdateBook.setEnabled(true)

                    Toast.makeText(
                        requireContext(),
                        "Book updated successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "You Can not Change Category",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return binding.root
    }

    private fun initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
    }

    private fun setupListeners() {
        binding.idAppBar.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.idAppBar.tvAppName.text = getString(R.string.update_book)

        val category = getCategoryList()
        setupSpinner(category)
        setupImagePicker()
    }

    private fun setDataEdittext(categoryID: Int, bookID: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Books")

        databaseReference.child(categoryID.toString()).child("BookList").child(bookID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val bookNameDatabase = snapshot.child("name").getValue(String::class.java)
                        val bookAuthorDatabase = snapshot.child("author").getValue(String::class.java)
                        val bookReleaseDateDatabase = snapshot.child("releaseDate").getValue(String::class.java)
                        val bookDescDatabase = snapshot.child("desc").getValue(String::class.java)
                        val bookImageDatabase = snapshot.child("url").getValue(String::class.java)

//                        Log.e("FirebaseData", "Book Name: $bookNameDatabase")
//                        Log.e("FirebaseData", "Book Author: $bookAuthorDatabase")
//                        Log.e("FirebaseData", "Book Release Date: $bookReleaseDateDatabase")
//                        Log.e("FirebaseData", "Book Description: $bookDescDatabase")
//                        Log.e("FirebaseData", "Book Image URL: $bookImageDatabase")

                        binding.apply {
                            etBookTitle.setText(bookNameDatabase ?: "No Name Available")
                            etAuthorName.setText(bookAuthorDatabase ?: "No Author Available")
                            etReleaseDate.setText(bookReleaseDateDatabase ?: "No Release Date Available")
                            etADescription.setText(bookDescDatabase ?: "No Description Available")


                            if (bookImageDatabase != null) {
                                Glide.with(binding.root).load(bookImageDatabase).into(binding.idTakeImage)
                            } else {
                                binding.idTakeImage.setImageResource(R.drawable.photo)
                            }

                            idSpinnerCategory.setSelection(categoryID)
                        }
                    } else {
                        Log.e("FirebaseError", "Book not found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", error.message)
                }
            })
    }


    private fun setupSpinner(category: List<String>) {
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, category)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.idSpinnerCategory.adapter = spinnerAdapter

        binding.idSpinnerCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    (view as? TextView)?.setTextColor(Color.BLACK)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }

    private fun setupImagePicker() {
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    filePath = result.data!!.data
                    filePath?.let { uri ->
                        val bitmap =
                            ImageDecoder.decodeBitmap(
                                ImageDecoder.createSource(
                                    requireContext().contentResolver,
                                    uri
                                )
                            )
                        binding.idTakeImage.setImageBitmap(bitmap)
                    }
                } else {
                    Log.e("ImagePicker", "Error: Image not selected")
                }
            }

        binding.idTakeImageUser.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
            imagePickerLauncher.launch(Intent.createChooser(intent, "Select image from here..."))
        }
    }

    private fun getCategoryList() = listOf(
        "Select Your Category",
        "Action Fiction",
        "Autobiography",
        "Biography",
        "Children's Literature",
        "Classics",
        "Dystopia",
        "Drama",
        "Crime Fiction",
        "Fantasy",
        "Horror",
        "Historical Fiction",
        "Mystery",
        "Comedy",
        "Poetry",
        "Science Fiction",
        "Thriller",
        "Romance"
    )

    private fun setupDatePicker() {
        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.etReleaseDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, dayOfMonth ->
                binding.etReleaseDate.setText(String.format("$dayOfMonth/${selectedMonth + 1}/$selectedYear"))
            }, year, month, day).show()
        }
    }

    private fun isValidInput(): Boolean {
        return validateField(binding.etBookTitle, "Please Enter Book Name") &&
                validateField(binding.etAuthorName, "Please Enter Author Name") &&
                validateField(binding.etReleaseDate, "Please Enter Release Date") &&
                validateField(binding.etADescription, "Please Enter Description")
    }

    private fun validateField(field: TextView, errorMessage: String): Boolean {
        return if (field.text.isEmpty()) {
            field.error = errorMessage
            false
        } else {
            true
        }
    }

    private fun updateBook(bookId: String) {
        if (filePath != null) {

            DialogUtils.loader(requireContext(),"Uploading...")

            val path = "Books/${UUID.randomUUID()}"
            val ref = storageReference.child(path)

            val spinner = binding.idSpinnerCategory.selectedItemPosition.toString()

            ref.putFile(filePath!!).addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { uri ->
                    databaseReference
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            DialogUtils.dismissDialog()
                            databaseReference.child(spinner)
                                .child("BookList").child(bookId).child("name")
                                .setValue(binding.etBookTitle.getText().toString())
                            databaseReference.child(spinner)
                                .child("BookList").child(bookId).child("author")
                                .setValue(binding.etAuthorName.getText().toString())
                            databaseReference.child(spinner)
                                .child("BookList").child(bookId).child("releaseDate")
                                .setValue(binding.etReleaseDate.getText().toString())
                            databaseReference.child(spinner)
                                .child("BookList").child(bookId).child("desc")
                                .setValue(binding.etADescription.getText().toString())
                            databaseReference.child(spinner)
                                .child("BookList").child(bookId).child("url")
                                .setValue(uri.toString())
                        }

                        override fun onCancelled(error: DatabaseError) {
                            DialogUtils.dismissDialog()
                        }
                    })
                }
            }
                .addOnFailureListener { e ->
                DialogUtils.dismissDialog()
                Toast.makeText(requireContext(), "Failed " + e.message, Toast.LENGTH_SHORT)
                    .show()
            }
                .addOnProgressListener { taskSnapshot ->
                val progress =
                    100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                DialogUtils.loader(requireContext(),"Uploaded " + progress.toInt() + "%")
            }
                .addOnCompleteListener{
                DialogUtils.dismissDialog()
            }
        } else {
            databaseReference.child(binding.idSpinnerCategory.selectedItemPosition.toString())
                .child("BookList")
                .child(bookId).child("name").setValue(binding.etBookTitle.text.toString())
            databaseReference.child(binding.idSpinnerCategory.selectedItemPosition.toString())
                .child("BookList")
                .child(bookId).child("author")
                .setValue(binding.etAuthorName.text.toString())
            databaseReference.child(binding.idSpinnerCategory.selectedItemPosition.toString())
                .child("BookList")
                .child(bookId).child("releaseDate")
                .setValue(binding.etReleaseDate.text.toString())
            databaseReference.child(binding.idSpinnerCategory.selectedItemPosition.toString())
                .child("BookList")
                .child(bookId).child("desc")
                .setValue(binding.etADescription.text.toString())
        }
    }
}
