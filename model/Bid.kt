package com.example.iiitbazaar.model

import com.google.firebase.Timestamp

data class Bid(
    val bidId: String = "",
    val postId: String = "",
    val bidderId: String = "",
    val bidderName: String = "",
    val bidderAvatar: String = "",
    val bidAmount: Double = 0.0,
    val timestamp: Timestamp = Timestamp.now()
)
