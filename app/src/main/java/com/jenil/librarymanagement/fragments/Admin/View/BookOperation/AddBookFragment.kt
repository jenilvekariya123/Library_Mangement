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
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.DialogUtils
import com.jenil.librarymanagement.databinding.FragmentAddBookBinding
import java.util.Locale
import java.util.UUID

class AddBookFragment : Fragment() {

    private lateinit var binding: FragmentAddBookBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var filePath: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBookBinding.inflate(layoutInflater)

        initFirebase()
        setupListeners()
        setupDatePicker()

        binding.idAppBar.tvAppName.text = getString(R.string.add_book)

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

        val category = getCategoryList()
        setupSpinner(category)

        setupImagePicker()

        binding.btnAddBook.setOnClickListener {
            if (isValidInput()) {
                addBookData(category)
                findNavController().navigate(R.id.action_addBookFragment_to_adminHomeFragment)
            }
        }
    }

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

    private fun setupSpinner(category: List<String>) {
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, category)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.idSpinnerCategory.adapter = spinnerAdapter

        binding.idSpinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (view as? TextView)?.setTextColor(Color.BLACK)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                filePath = result.data!!.data
                filePath?.let { uri ->
                    val bitmap =
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, uri))
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
        "Select Your Category", "Action Fiction", "Autobiography", "Biography", "Children's Literature",
        "Classics", "Dystopia", "Suspense", "Crime Fiction", "Fantasy", "Horror", "Historical Fiction", "Mystery", "Comedy", "Poetry", "Science Fiction",
        "Thriller", "Romance"
    )

    private fun addBookData(category: List<String>) {
        val position = binding.idSpinnerCategory.selectedItemPosition

        if (position != 0) {
            val currentCategoryCounterRef = databaseReference.child("Books").child((position).toString()).child("BookCounter")

            currentCategoryCounterRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    var currentId = currentData.getValue(Int::class.java)
                    if (currentId == null) {
                        currentId = 0
                    }
                    currentData.value = currentId + 1
                    return Transaction.success(currentData)
                }

                override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                    if (error != null) {
                        Toast.makeText(requireContext(), "Failed to update book ID for current category", Toast.LENGTH_SHORT).show()
                    } else {
                        val bookId = currentData?.getValue(Int::class.java) ?: 1
                        uploadImage(bookId.toString(), category)


                        Toast.makeText(requireContext(), "Current Category BookCounter: $bookId", Toast.LENGTH_LONG).show()
                        printNextCategoryBookCounter(position + 1)
                    }
                }
            })
        } else {
            Toast.makeText(requireContext(), "Select Category First", Toast.LENGTH_SHORT).show()
        }
    }

    private fun printNextCategoryBookCounter(nextPosition: Int) {
        if (nextPosition < getCategoryList().size) {
            // Reference for the next category's book counter
            val nextCategoryCounterRef = databaseReference.child("Books").child(nextPosition.toString()).child("BookCounter")

            nextCategoryCounterRef.get().addOnSuccessListener { snapshot ->
                val nextCategoryCounter = snapshot.getValue(Int::class.java) ?: 0

                // Print the next category's BookCounter
                Toast.makeText(requireContext(), "Next Category BookCounter: $nextCategoryCounter", Toast.LENGTH_LONG).show()

            }.addOnFailureListener { error ->
                Log.e("NextCategoryCounter", "Failed to retrieve next category counter", error)
            }
        }
    }



    private fun uploadImage(key: String, category: List<String>) {
        filePath?.let { uri ->
            DialogUtils.loader(requireContext(),"Uploading...")

            val path = "Books/${UUID.randomUUID()}"
            val ref = storageReference.child(path)

            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveBookData(downloadUri.toString(), key, category)
                    }.addOnFailureListener {
                        DialogUtils.dismissDialog()
                        Toast.makeText(requireContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { _ ->
                    DialogUtils.dismissDialog()
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener {
                    DialogUtils.dismissDialog()
                }
        }
    }

    private fun saveBookData(imageUri: String, bookId: String, category: List<String>) {
        val selectedPosition = binding.idSpinnerCategory.selectedItemPosition
        category[selectedPosition].lowercase(Locale.ROOT)

        val bookRef = databaseReference.child("Books").child(selectedPosition.toString()).child("BookList").child(bookId)

        bookRef.setValue(mapOf(
            "name" to binding.etBookTitle.text.toString(),
            "author" to binding.etAuthorName.text.toString(),
            "desc" to binding.etADescription.text.toString(),
            "releaseDate" to binding.etReleaseDate.text.toString(),
            "url" to imageUri,
            "id" to bookId
        ))
    }

    private fun isValidInput(): Boolean {
        return validateField(binding.etBookTitle, "Please Enter Book Name") &&
                validateField(binding.etAuthorName, "Please Enter Author Name") &&
                validateField(binding.etReleaseDate, "Please Enter Release Date") &&
                validateField(binding.etADescription, "Please Enter Description")
    }

    private fun validateField(field: TextView, errorMessage: String): Boolean {
        return if (field.text.toString().isEmpty()) {
            field.error = errorMessage
            field.requestFocus()
            field.clearFocus()
            false
        } else true
    }
}
