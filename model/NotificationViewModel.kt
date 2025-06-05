package com.example.iiitbazaar.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class NotificationViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> get() = _notifications
    private var listener: ListenerRegistration? = null

    fun startListening(userId: String) {
        listener?.remove()
        listener = db.collection("users")
            .document(userId)
            .collection("notifications")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(Notification::class.java)?.apply { id = it.id } }

                    _notifications.value = list
                }
            }
    }

    fun sendNotification(toUserId: String, message: String, route: String) {
        val db = FirebaseFirestore.getInstance()

        // Generate a unique ID (hash of message + route + optional timestamp for uniqueness)
        val notificationId = "${message}_${route}".hashCode().toString()

        val notification = Notification(
            id = notificationId,
            message = message,
            route = route,
            timestamp = Timestamp.now(),
            isRead = false
        )

        val notifRef = db.collection("users")
            .document(toUserId)
            .collection("notifications")
            .document(notificationId)

        notifRef.get().addOnSuccessListener { doc ->
            if (!doc.exists()) {
                notifRef.set(notification)
            }
        }
    }


    fun markAsRead(userId: String, notificationId: String) {
        db.collection("users")
            .document(userId)
            .collection("notifications")
            .document(notificationId)
            .update("isRead", true)
    }

    fun clearAllNotifications(userId: String) {
        db.collection("users")
            .document(userId)
            .collection("notifications")
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.forEach { doc ->
                    doc.reference.delete()
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}
