package com.example.iiitbazaar.ChatRepo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override suspend fun sendMessage(chatId: String, senderId: String, message: String) {
        val messageData = mapOf(
            "senderId" to senderId,
            "text" to message,
            "timestamp" to System.currentTimeMillis()
        )

        try {
            // Add message
            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(messageData)
                .await()

            // Update chat metadata
            firestore.collection("chats")
                .document(chatId)
                .update(
                    mapOf(
                        "lastMessage" to message,
                        "timestamp" to System.currentTimeMillis()
                    )
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getMessages(chatId: String): Flow<List<ChatMessage>> = callbackFlow {
        val ref = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val messages = snapshot?.documents?.mapNotNull { doc ->
                val senderId = doc.getString("senderId")
                val text = doc.getString("text")
                val timestamp = doc.getLong("timestamp")
                if (senderId != null && text != null && timestamp != null) {
                    ChatMessage(senderId, text, timestamp)
                } else null
            } ?: emptyList()

            trySend(messages).isSuccess
        }

        awaitClose { listener.remove() }
    }
}
