package com.example.iiitbazaar.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor() : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun createPost(
        imageUrl: String,
        description: String,
        isBiddingEnabled: Boolean,
        basePrice: Double,
        biddingEndTime: Timestamp?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onFailure("User not logged in")
            return
        }

        val uid = user.uid
        val userDocRef = db.collection("users").document(uid)

        viewModelScope.launch {
            userDocRef.get().addOnSuccessListener { document ->
                val name = document.getString("name") ?: "Unknown"
                val profileImageUrl = document.getString("profileImageUrl") ?: ""
                val postId = UUID.randomUUID().toString()

                val post = Post(
                    postId = postId,
                    userId = uid,
                    username = name,
                    profileImageUrl = profileImageUrl,
                    imageUrl = imageUrl,
                    description = description,
                    timestamp = Timestamp.now(),
                    isBiddingEnabled = isBiddingEnabled,
                    basePrice = basePrice,
                    biddingEndTime = biddingEndTime
                )

                db.collection("posts").document(postId)
                    .set(post)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onFailure(e.message ?: "Unknown error") }

            }.addOnFailureListener { e ->
                onFailure("Failed to fetch user details: ${e.message}")
            }
        }
    }

    fun deletePost(
        postId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        FirebaseFirestore.getInstance()
            .collection("posts")
            .document(postId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

}
