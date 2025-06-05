package com.example.iiitbazaar.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostViewModel : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    init {
        fetchPosts()
    }

    fun fetchPosts(onDone: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val db = FirebaseFirestore.getInstance()
                val snapshot = db.collection("posts")
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()
                val postList = snapshot.documents.mapNotNull { it.toObject(Post::class.java) }
                _posts.value = postList
            } catch (e: Exception) {
                // Optionally log the error
            } finally {
                onDone?.invoke()
            }
        }
    }
}
