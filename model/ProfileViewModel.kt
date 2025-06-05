package com.example.iiitbazaar.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchUserProfile(uid: String) {
        _isLoading.value = true
        _error.value = null

        FirebaseFirestore.getInstance().collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                _isLoading.value = false
                if (doc.exists()) {
                    val user = doc.toObject(User::class.java)
                    _userProfile.value = user
                } else {
                    _userProfile.value = null
                    _error.value = "User not found."
                }
            }
            .addOnFailureListener { exception ->
                _isLoading.value = false
                _userProfile.value = null
                _error.value = exception.message ?: "Failed to fetch user profile."
            }
    }

    fun resetProfileState() {
        _userProfile.value = null
        _error.value = null
        _isLoading.value = false
    }

    fun updateName(userId: String, newName: String) {
        _isLoading.value = true
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(userId)
            .update("name", newName)
            .addOnSuccessListener {
                // UPDATE all posts by this user
                db.collection("posts")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val batch = db.batch()
                        for (document in querySnapshot.documents) {
                            batch.update(document.reference, "username", newName)
                        }
                        batch.commit()
                    }
            }
            .addOnFailureListener {
                _error.value = "Failed to update name."
            }
            .addOnCompleteListener {
                _isLoading.value = false
                fetchUserProfile(userId)
            }
    }

    fun updateAvatar(userId: String, newImageUrl: String) {
        _isLoading.value = true
        val db = FirebaseFirestore.getInstance()

        // Step 1: Update profileImageUrl in the users collection
        db.collection("users")
            .document(userId)
            .update("avatar", newImageUrl)
            .addOnSuccessListener {
                // Step 2: Update profileImageUrl in all posts made by the user
                db.collection("posts")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val batch = db.batch()
                        for (document in querySnapshot.documents) {
                            val postRef = document.reference
                            batch.update(postRef, "profileImageUrl", newImageUrl)
                        }
                        batch.commit()
                            .addOnSuccessListener {
                                // Step 3: Update the locally cached profile
                                _userProfile.value = _userProfile.value?.copy(avatar = newImageUrl)
                                _isLoading.value = false
                            }
                            .addOnFailureListener { batchError ->
                                _error.value = batchError.message ?: "Failed to update profile image in posts."
                                _isLoading.value = false
                            }
                    }
                    .addOnFailureListener { postError ->
                        _error.value = postError.message ?: "Failed to fetch user posts."
                        _isLoading.value = false
                    }
            }
            .addOnFailureListener { userError ->
                _error.value = userError.message ?: "Failed to update profile image in user profile."
                _isLoading.value = false
            }
    }

}





