package com.example.iiitbazaar.model

import com.google.firebase.Timestamp


data class Post(

    val userId: String = "",
    val postId: String = "",
    val username: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val profileImageUrl: String = "",
    val timestamp: Timestamp = Timestamp.now(),

    // Bidding-related fields
    @get:com.google.firebase.firestore.PropertyName("biddingEnabled")
    @set:com.google.firebase.firestore.PropertyName("biddingEnabled")
    var isBiddingEnabled: Boolean = false,

    val basePrice: Double = 0.0,
    val biddingEndTime: Timestamp? = null
)