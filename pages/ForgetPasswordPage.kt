package com.example.iiitbazaar.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.iiitbazaar.ui.theme.DancingScript
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgotPasswordPage(
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Gradient Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
                )
            ),

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "✨ Designed by Aditya ✨",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            fontSize = 18.sp,
            color = Color.White,
            fontFamily = DancingScript
        )

        // Card-Like Container
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔐 Reset Your Password",
                    fontSize = 22.sp,
                    color = Color(0xFF4A00E0),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Enter your email") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = "Email Icon")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (email.isNotEmpty()) {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Reset link sent to your email",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.popBackStack()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Error: ${task.exception?.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                context,
                                "Please enter your email",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A00E0))
                ) {
                    Text("Send Reset Link")
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = {
                    navController.popBackStack()
                }) {
                    Text("Back to Login",  color = Color(0xFF4A00E0))
                }
            }
        }
    }
}
