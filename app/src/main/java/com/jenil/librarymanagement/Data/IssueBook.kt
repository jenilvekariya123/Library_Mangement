package com.jenil.librarymanagement.Data

data class IssueBook(
    val fullName: String,
    val studId: String,
    val bookNo: String,
    val category: String,
    val bookName: String,
    val imageUrl : String,
    val categoryName: String,
    val issueDate: String,
    val returnDate: String,
)
