package com.example.iiitbazaar.ChatRepo

import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(chatId: String, senderId: String, message: String)
    fun getMessages(chatId: String): Flow<List<ChatMessage>>
}
