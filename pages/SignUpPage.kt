package com.example.iiitbazaar.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.iiitbazaar.AuthViewModel
import com.example.iiitbazaar.AuthState
import com.example.iiitbazaar.ui.theme.DancingScript

@Composable
fun SignUpPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AuthState.Authenticate -> navController.navigate("home")
            is AuthState.EmailVerificationSent -> navController.navigate("verify_email")
            is AuthState.Error -> Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            is AuthState.Info -> Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
                )
            )
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

        Column(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🌟 Welcome Text
            Text(
                text = "Create Account at",
                fontSize = 36.sp,
                color = Color.White
            )
            Text(
                text = "IIIT Bazaar",
                fontSize = 36.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Name Icon")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Email Icon")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Password Icon")
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle Password Visibility"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            authViewModel.signup(name, email, password)
                        },
                        enabled = authState.value != AuthState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A00E0))
                    ) {
                        Text("Create Account")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = {
                        navController.navigate("login")
                    }) {
                        Text("Already have an account? Login",  color = Color(0xFF4A00E0))
                    }
                }
            }
    }
}
}
