package com.example.iiitbazaar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.iiitbazaar.ui.theme.IIITBazaarTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        val authViewModel : AuthViewModel by viewModels()
        setContent {
            IIITBazaarTheme  {
                val user = FirebaseAuth.getInstance().currentUser
                val currentUserId = user?.uid ?: ""

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyAppNavigation(currentUserId = currentUserId,modifier =  Modifier.padding(innerPadding),authViewModel = authViewModel)
                }
            }
        }
    }
}