package com.example.iiitbazaar.model

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.State


class HomeViewModel : ViewModel() {
    private val _userName = mutableStateOf("")
    val userName: State<String> = _userName

    init {
        loadUserName()
    }

    private fun loadUserName() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name")
                _userName.value = name ?: ""
            }
            .addOnFailureListener {
                Log.e("HomeViewModel", "Failed to load user name: ${it.message}")
            }
    }
}
