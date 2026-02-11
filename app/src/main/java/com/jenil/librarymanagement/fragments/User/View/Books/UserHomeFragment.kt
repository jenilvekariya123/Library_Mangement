package com.jenil.librarymanagement.fragments.User.View.Books

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jenil.librarymanagement.Data.BookInfo
import com.jenil.librarymanagement.Data.CategoryModel
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.Utils.DialogUtils
import com.jenil.librarymanagement.Utils.SharedPreference
import com.jenil.librarymanagement.activities.MainActivity
import com.jenil.librarymanagement.adapter.HomeCategoryAdapter
import com.jenil.librarymanagement.adapter.NewArrivalAdapter
import com.jenil.librarymanagement.databinding.FragmentUserHomeBinding


class UserHomeFragment : Fragment() {

    private lateinit var binding : FragmentUserHomeBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserHomeBinding.inflate(layoutInflater)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference()

        val studId = SharedPreference.getString("StudentID", "")

        Log.e("TAG","STUDENT ID ::: $studId")

        setHeaderData(studId)
        setDrawableData(studId)
        setBestBookData()
        setNewArrivalData()
        setCategoryData()

        binding.btnSeeAll.setOnClickListener {
            findNavController().navigate(R.id.action_userHomeFragment_to_userCategoryFragment)
        }

        binding.ivMenu.setOnClickListener {
            openDrawer(binding.idDrawerLayout)
        }

        binding.ivSave.setOnClickListener {
            findNavController().navigate(R.id.action_userHomeFragment_to_saveBookFragment)
            SharedPreference.putString("StudentID",studId)
        }

        binding.idSearchView.setOnClickListener {
            findNavController().navigate(R.id.action_userHomeFragment_to_searchFragment)
            SharedPreference.putString("StudentID",studId)
        }

        return binding.root
    }

    private fun setHeaderData(studId: String?) {
        if (studId != null){
            databaseReference = FirebaseDatabase.getInstance().getReference()
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.child("Users").child(studId).child("fullName").getValue(String::class.java)
                    binding.headerTitle.tvLibraryName.text = userName
                    binding.tvUserName.text = userName
                    binding.headerTitle.tvLibraryId.text = getString(R.string.student_id, studId)
                }
                override fun onCancelled(error: DatabaseError) {  }
            })
        }
    }

    private fun setDrawableData(studId: String?) {
        binding.layout.apply {
            home.setOnClickListener{
                closeDrawer(binding.idDrawerLayout)
            }
            Profile.setOnClickListener {
                findNavController().navigate(R.id.action_userHomeFragment_to_userProfileFragment)
                SharedPreference.putString("StudentID",studId)
            }
            issue.setOnClickListener {
                findNavController().navigate(R.id.action_userHomeFragment_to_issueBookFragment)
                SharedPreference.putString("StudentID",studId)
            }
            save.setOnClickListener {
                findNavController().navigate(R.id.action_userHomeFragment_to_saveBookFragment)
                SharedPreference.putString("StudentID",studId)
            }
            search.setOnClickListener {
                findNavController().navigate(R.id.action_userHomeFragment_to_searchFragment)
                SharedPreference.putString("StudentID",studId)
            }
            setting.setOnClickListener {
                findNavController().navigate(R.id.action_userHomeFragment_to_userSettingFragment)
                SharedPreference.putString("StudentID",studId)
            }
            about.setOnClickListener {
                findNavController().navigate(R.id.action_userHomeFragment_to_aboutUsFragment)
            }
            logout.setOnClickListener {
                DialogUtils.showDialog(requireActivity(),
                    "Are you sure you want to logout?","Log Out","Cancel",
                    object : DialogUtils.OnDialogButtonClick{
                        override fun onPositiveButtonClick() {
                            SharedPreference.clearSharedPreference()
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                            requireActivity().finishAffinity()
                        }

                        override fun onNegativeButtonClick() {
                            DialogUtils.dismissDialog()
                        }
                    })
            }

        }
    }

    private fun setBestBookData() {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Books")

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookName = snapshot.child("3").child("BookList").child("3").child("name").getValue(String::class.java)
                    val bookDesc = snapshot.child("3").child("BookList").child("3").child("desc").getValue(String::class.java)
                    val bookUrl = snapshot.child("3").child("BookList").child("3").child("url").getValue(String::class.java)
                    val bookId = snapshot.child("3").child("BookList").child("3").child("id").getValue(String::class.java)

                    binding.apply {
                        tvBestBookName.text = bookName
                        tvBestBookDesc.text = bookDesc

                        Glide.with(binding.root).apply {
                            load(bookUrl)
                                .placeholder(R.drawable.book6)
                                .into(ivImageBest)
                        }

                        cardView.setOnClickListener {
                            SharedPreference.putString("BookID","3")
                            findNavController().navigate(R.id.action_userHomeFragment_to_bookDetailsFragment)
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG","Book Data Error:: ${error.message}" )
                }

            })
    }

    private fun setNewArrivalData() {
        val bookList = mutableListOf<BookInfo>()

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Books")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (categorySnapshot in snapshot.children) {
                    val bookListSnapshot = categorySnapshot.child("BookList")
                    for (bookSnapshot in bookListSnapshot.children) {
                        val book = BookInfo(
                            id = bookSnapshot.child("id").getValue(String::class.java),
                            name = bookSnapshot.child("name").getValue(String::class.java),
                            author = bookSnapshot.child("author").getValue(String::class.java),
                            releaseDate = bookSnapshot.child("releaseDate").getValue(String::class.java),
                            desc = bookSnapshot.child("desc").getValue(String::class.java),
                            url = bookSnapshot.child("url").getValue(String::class.java)
                        )
                        bookList.add(book)
                    }
                }
                setupRecyclerView(bookList)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "Error retrieving new arrival data: ${error.message}")
            }
        })
    }

    private fun setupRecyclerView(bookList: List<BookInfo>) {
        val adapter = NewArrivalAdapter(bookList) { selectedBook ->
            SharedPreference.putString("BookID", selectedBook.id!!)
            findNavController().navigate(R.id.action_userHomeFragment_to_bookDetailsFragment)
        }
        binding.idRecyclerNewArrival.adapter = adapter
        binding.idRecyclerNewArrival.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setCategoryData() {
        val categoryList = mutableListOf<CategoryModel>()

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Books")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (categorySnap in snapshot.children){
                    val category = CategoryModel(
                        id = categorySnap.key.toString(),
                        name = categorySnap.child("category").getValue(String::class.java).toString(),
                        imageUrl = categorySnap.child("url").getValue(String::class.java).toString(),
                    )
                    categoryList.add(category)
                }

                val onlyLimitList = categoryList.take(4)
                categoryRecycler(onlyLimitList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG","onCancelled :: ${error.message}")
            }

        })
    }

    private fun categoryRecycler(categoryList: List<CategoryModel>) {
        val adapter = HomeCategoryAdapter(categoryList) {
            selectedCategory ->
            SharedPreference.putString("CategoryId", selectedCategory.id)
            findNavController().navigate(R.id.action_userHomeFragment_to_userBookListFragment)

        }
        binding.idRecyclerHomeCategory.adapter = adapter
        binding.idRecyclerHomeCategory.layoutManager = GridLayoutManager(requireContext(),2)
    }



    private fun openDrawer(drawerLayout: DrawerLayout){
        drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun closeDrawer(drawerLayout: DrawerLayout){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onPause() {
        super.onPause()
        closeDrawer(binding.idDrawerLayout)
    }

}