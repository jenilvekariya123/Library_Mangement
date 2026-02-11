package com.jenil.librarymanagement.fragments.Admin.View.BookOperation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jenil.librarymanagement.Data.BookInfo
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.DialogUtils
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.adapter.BooksAdapter
import com.jenil.librarymanagement.databinding.FragmentBookListBinding

class BookListFragment : Fragment() {

    private lateinit var binding: FragmentBookListBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var myDB: DatabaseReference
    private var booksList: MutableList<BookInfo> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookListBinding.inflate(inflater)

        binding.idAppBar.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val categoryID = SharedPreference.getInt("categoryID", 0)

        firebaseDatabase = FirebaseDatabase.getInstance()
        myDB = firebaseDatabase.getReference("Books").child(categoryID.toString())

        binding.idRecyclerBook.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = BooksAdapter(booksList,categoryID,
                onUpdateClick = { bookId, categoryId ->
                    SharedPreference.putInt("categoryID", categoryId)
                    SharedPreference.putString("bookID", bookId)
                    findNavController().navigate(R.id.action_bookListFragment_to_updateBookFragment)
                    Log.e("BookListFragment", "Book ID: $bookId")

            }, onDeleteClick = { bookId ->
                SharedPreference.putString("bookID", bookId)

                    DialogUtils.showDialog(requireActivity(),"Are you sure you want to delete this book?","Delete","Cancel",
                        object : DialogUtils.OnDialogButtonClick {
                            override fun onPositiveButtonClick() {
                                deleteBook(bookId)
                            }

                            override fun onNegativeButtonClick() {
                               DialogUtils.dismissDialog()
                            }

                        })
            })
        }


        binding.idProgressBar.visibility = View.VISIBLE
        fetchBooks()

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteBook(bookId: String) {
        val bookRef = myDB.child("BookList").child(bookId)

        bookRef.removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                DialogUtils.dismissDialog()
                Toast.makeText(requireContext(),"Delete Book Successful",Toast.LENGTH_SHORT).show()
                fetchBooks()
                binding.idRecyclerBook.adapter!!.notifyDataSetChanged()
            }else{
                Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchBooks() {
        myDB.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                booksList.clear()

                if (snapshot.exists()) {
                    val categoryName = snapshot.child("category").getValue(String::class.java)
                    binding.idAppBar.tvAppName.text = categoryName

                    val bookListSnapshot = snapshot.child("BookList")
                    for (bookSnapshot in bookListSnapshot.children) {
                        val bookInfo = bookSnapshot.getValue(BookInfo::class.java)
                        val bookId = bookSnapshot.key
                        SharedPreference.putString("bookID", bookId.toString())
                        if (bookInfo != null) {
                            booksList.add(bookInfo)
                            bookInfo.id = bookId
                        }
                    }


                    if (booksList.isEmpty()) {
                        binding.idProgressBar.visibility = View.GONE
                        binding.tvNoFound.visibility = View.VISIBLE
                        binding.idLottieEmpty.visibility = View.VISIBLE
                        binding.idRecyclerBook.visibility = View.GONE
                    } else {
                        binding.idProgressBar.visibility = View.GONE
                        binding.tvNoFound.visibility = View.GONE
                        binding.idLottieEmpty.visibility = View.GONE
                        binding.idRecyclerBook.visibility = View.VISIBLE
                        (binding.idRecyclerBook.adapter as? BooksAdapter)?.notifyDataSetChanged()
                    }
                }

                binding.idProgressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                binding.idProgressBar.visibility = View.GONE
                Log.e("BookListFragment", "Error: ${error.message}")
            }
        })
    }
}
