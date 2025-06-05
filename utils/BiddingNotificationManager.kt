package com.example.iiitbazaar.util

import android.util.Log
import com.example.iiitbazaar.model.Bid
import com.example.iiitbazaar.model.NotificationViewModel
import com.example.iiitbazaar.model.Post
import com.example.iiitbazaar.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class BiddingNotificationManager(private val notificationViewModel: NotificationViewModel) {

    private val db = FirebaseFirestore.getInstance()

    fun checkAndSendNotifications(post: Post) {
        if (!post.isBiddingEnabled || post.biddingEndTime == null) return

        val now = com.google.firebase.Timestamp.now()
        if (now < post.biddingEndTime) return

        val bidsRef = db.collection("posts").document(post.postId).collection("bids")
        bidsRef.get().addOnSuccessListener { snapshot ->
            val bidList = snapshot.mapNotNull { it.toObject(Bid::class.java) }
                .sortedByDescending { it.bidAmount }

            if (bidList.isEmpty()) {
                notificationViewModel.sendNotification(
                    toUserId = post.userId,
                    message = "⏰ Bidding ended with no bids.",
                    route = "userProfile/${post.userId}"
                )
            } else {
                val highestBid = bidList.first()

                // Fetch post owner's name
                db.collection("users").document(post.userId).get().addOnSuccessListener { doc ->
                    val owner = doc.toObject<User>()
                    val ownerName = owner?.name ?: "the owner"

                    notificationViewModel.sendNotification(
                        toUserId = highestBid.bidderId,
                        message = "✅ You won the bid! Contact $ownerName.",
                        route = "userProfile/${post.userId}"
                    )

                    notificationViewModel.sendNotification(
                        toUserId = post.userId,
                        message = "📢 ${highestBid.bidderName} has the highest bid. Contact them.",
                        route = "userProfile/${highestBid.bidderId}"
                    )
                }
            }

        }.addOnFailureListener {
            Log.e("BiddingManager", "Failed to fetch bids: ${it.message}")
        }
    }
}
