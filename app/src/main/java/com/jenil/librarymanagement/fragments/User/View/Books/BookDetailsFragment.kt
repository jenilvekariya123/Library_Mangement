package com.jenil.librarymanagement.fragments.User.View.Books

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.DialogUtils
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.databinding.FragmentBookDetailsBinding
import java.text.SimpleDateFormat
import java.util.Date


class BookDetailsFragment : Fragment() {

    private lateinit var binding: FragmentBookDetailsBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookDetailsBinding.inflate(layoutInflater)

        val bookId = SharedPreference.getString("BookID","")
        val categoryID = SharedPreference.getString("CategoryId","")
        val studID = SharedPreference.getString("StudentID","")

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnIssueBook.setOnClickListener {
            DialogUtils.loader(requireContext(),"Loading...")
            SharedPreference.putString("BookID",bookId.toString())
            SharedPreference.putString("CategoryId",categoryID.toString())
            SharedPreference.putString("StudentID",studID.toString())
            findNavController().navigate(R.id.action_bookDetailsFragment_to_issueBookFragment)
            enterData(categoryID.toString(),bookId.toString(),studID.toString())

        }

        setDataBooks(bookId.toString(),categoryID.toString())
        saveButton(studID.toString(),bookId.toString(),categoryID.toString())
        setSeeMore()

        return binding.root
    }


    private fun saveButton(studID: String, bookId: String, categoryID: String) {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(studID).child("SavedBook")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isBookSaved = false

                for (savedBookSnapshot in snapshot.children) {
                    val savedBookId = savedBookSnapshot.child("bookNo").getValue(String::class.java)
                    if (savedBookId == bookId) {
                        isBookSaved = true
                        break
                    }
                }

                if (isBookSaved) {
                    binding.ivFavBorder.setImageResource(R.drawable.ic_fav_fill)
                } else {
                    binding.ivFavBorder.setImageResource(R.drawable.ic_love)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "onCancelled :: ${error.message}")
            }
        })

        binding.ivFavBorder.setOnClickListener {
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var isBookSaved = false
                    var savedBookKey: String? = null

                    for (savedBookSnapshot in snapshot.children) {
                        val savedBookId = savedBookSnapshot.child("bookNo").getValue(String::class.java)
                        if (savedBookId == bookId) {
                            isBookSaved = true
                            savedBookKey = savedBookSnapshot.key
                            break
                        }
                    }

                    if (isBookSaved) {

                        databaseReference.child(savedBookKey!!).removeValue()
                        binding.ivFavBorder.setImageResource(R.drawable.ic_love)
                        Toast.makeText(requireContext(), "Book Removed from Favorites", Toast.LENGTH_SHORT).show()
                    } else {
                        val nextKey = (snapshot.childrenCount + 1).toString()

                        databaseReference.child(nextKey).child("category").setValue(categoryID)
                        databaseReference.child(nextKey).child("bookNo").setValue(bookId)
                        binding.ivFavBorder.setImageResource(R.drawable.ic_fav_fill)
                        Toast.makeText(requireContext(), "Book Saved to Favorites", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "onCancelled :: ${error.message}")
                }
            })
        }
    }

    private fun enterData(categoryId: String, bookId: String, studID: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("IssueBook").child(studID)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(snapshot: DataSnapshot) {
                DialogUtils.dismissDialog()

                val sdf = SimpleDateFormat("dd/MM/yyyy")
                val currentDateAndTime = sdf.format(Date())

                val childCount = snapshot.childrenCount.toInt()
                databaseReference.child(childCount.toString()).child("category").setValue(categoryId)
                databaseReference.child(childCount.toString()).child("bookNo").setValue(bookId)
                databaseReference.child(childCount.toString()).child("studID").setValue(studID)
                databaseReference.child(childCount.toString()).child("issueDate").setValue(currentDateAndTime)

                Toast.makeText(requireContext(), "Book issued successfully", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                DialogUtils.dismissDialog()
            }
        })
    }

    private fun setSeeMore() {
        var isExpend = false
        binding.tvSeeMore.setOnClickListener {
            if (!isExpend){
                binding.tvBookDesc.maxLines = 25
                binding.tvSeeMore.text = getString(R.string.read_less)
                isExpend = true
            }else{
                binding.tvBookDesc.maxLines = 3
                binding.tvSeeMore.text = getString(R.string.read_more)
                isExpend = false
            }
        }
    }

    private fun setDataBooks(bookID: String, categoryID: String) {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference().child("Books").child(categoryID)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val categoryName = snapshot.child("category").getValue(String::class.java)

                    val bookSnapshot = snapshot.child("BookList").child(bookID)
                    val bookName = bookSnapshot.child("name").getValue(String::class.java)
                    val bookAuthor = bookSnapshot.child("author").getValue(String::class.java)
                    val bookReDate = bookSnapshot.child("releaseDate").getValue(String::class.java)
                    val bookDesc = bookSnapshot.child("desc").getValue(String::class.java)
                    val bookUrl = bookSnapshot.child("url").getValue(String::class.java)

                    binding.apply {
                        tvBookName.text = bookName
                        tvBookAuthorName.text = bookAuthor
                        tvBookReleaseDate.text = bookReDate
                        tvBookDesc.text = bookDesc
                        tvBookCategory.text = categoryName


                        Glide.with(root)
                            .load(bookUrl)
                            .placeholder(R.drawable.ic_place)
                            .into(ivBookImage)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BookDetailsFragment", "Error fetching book details: ${error.message}")
            }
        })
    }
}