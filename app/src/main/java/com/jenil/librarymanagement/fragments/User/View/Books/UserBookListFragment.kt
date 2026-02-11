package com.jenil.librarymanagement.fragments.User.View.Books

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jenil.librarymanagement.Data.BookInfo
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.adapter.HomeBooksAdapter
import com.jenil.librarymanagement.databinding.FragmentUserBookListBinding


class UserBookListFragment : Fragment() {

    private lateinit var binding : FragmentUserBookListBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBookListBinding.inflate(layoutInflater)

        binding.idAppBar.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val categoryId = SharedPreference.getString("CategoryId","")
        setBookData(categoryId.toString())

        return binding.root
    }

    private fun setBookData(categoryId : String?){
        val bookList = mutableListOf<BookInfo>()

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference().child("Books").child(categoryId.toString())

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val categoryName = snapshot.child("category").getValue(String::class.java)
                    binding.idAppBar.tvAppName.text = categoryName

                    val bookListSnapshot = snapshot.child("BookList")
                    for (bookSnapshot in bookListSnapshot.children){
                        val bookInfo = bookSnapshot.getValue(BookInfo::class.java)

                        if (bookInfo != null){
                            bookList.add(bookInfo)
                        }
                    }
                    setRecyclerData(bookList,categoryId)
                }

                if (bookList.isEmpty()){
                    binding.idProgressBar.visibility = View.GONE
                    binding.tvNoFound.visibility = View.VISIBLE
                    binding.idLottieEmpty.visibility = View.VISIBLE
                    binding.idRecyclerHomeBooks.visibility = View.GONE
                }else{
                    binding.idProgressBar.visibility = View.GONE
                    binding.tvNoFound.visibility = View.GONE
                    binding.idLottieEmpty.visibility = View.GONE
                    binding.idRecyclerHomeBooks.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.idProgressBar.visibility = View.GONE
                binding.tvNoFound.visibility = View.VISIBLE
                binding.idLottieEmpty.visibility = View.VISIBLE
                binding.idRecyclerHomeBooks.visibility = View.GONE
                Log.e("TAG","onCancelled :: ${error.message}")
            }

        })

    }

    private fun setRecyclerData(bookList : MutableList<BookInfo>,categoryId: String?){
        val adapter = HomeBooksAdapter(bookList){ selectedBookId ->
            SharedPreference.putString("BookID",selectedBookId.id)
            SharedPreference.putString("CategoryId",categoryId.toString())
            findNavController().navigate(R.id.action_userBookListFragment_to_bookDetailsFragment)
        }
        binding.idRecyclerHomeBooks.adapter = adapter
        binding.idRecyclerHomeBooks.layoutManager = GridLayoutManager(requireContext(),3)
    }


}