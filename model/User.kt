package com.example.iiitbazaar.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val avatar: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
