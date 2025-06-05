package com.example.iiitbazaar.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iiitbazaar.ChatRepo.ChatMessage
import com.example.iiitbazaar.ChatRepo.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _chatUser = MutableStateFlow<User?>(null)
    val chatUser: StateFlow<User?> = _chatUser.asStateFlow()

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            chatRepository.getMessages(chatId).collect { msgs ->
                _messages.value = msgs
            }
        }
    }

    fun sendMessage(chatId: String, senderId: String, message: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(chatId, senderId, message)
        }
    }

    fun fetchUsers(currentUserId: String, chatUserId: String) {
        viewModelScope.launch {
            _currentUser.value = getUserById(currentUserId)
            _chatUser.value = getUserById(chatUserId)
        }
    }

    private suspend fun getUserById(userId: String): User? {
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
