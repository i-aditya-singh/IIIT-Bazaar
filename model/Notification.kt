package com.example.iiitbazaar.model

import com.google.firebase.Timestamp

data class Notification(
    var id: String = "",
    val message: String = "",
    val route: String = "",
    val senderName: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val isRead: Boolean = false
)
