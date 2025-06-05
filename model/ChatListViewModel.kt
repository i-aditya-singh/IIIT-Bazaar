package com.example.iiitbazaar.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatListViewModel : ViewModel() {
    private val _chatUsers = MutableStateFlow<List<User>>(emptyList())
    val chatUsers = _chatUsers.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    fun fetchChatUsers(currentUserId: String) {
        viewModelScope.launch {
            val chatUserIdsSnapshot = db.collection("users")
                .document(currentUserId)
                .collection("chats")
                .get()
                .await()

            val userList = mutableListOf<User>()

            for (doc in chatUserIdsSnapshot) {
                val userId = doc.id
                val userSnap = db.collection("users").document(userId).get().await()
                userSnap.toObject(User::class.java)?.let {
                    userList.add(it)
                }
            }

            _chatUsers.value = userList
        }
    }
}
