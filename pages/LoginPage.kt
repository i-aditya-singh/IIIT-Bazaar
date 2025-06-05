package com.example.iiitbazaar.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.iiitbazaar.AuthState
import com.example.iiitbazaar.AuthViewModel
import com.example.iiitbazaar.ui.theme.DancingScript

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticate -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
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
        Box(
            modifier = Modifier
                .fillMaxSize()
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
        }

        Column(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome Back to",
                fontSize = 36.sp,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "IIIT Bazaar",
                fontSize = 36.sp,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

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

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = {
                            navController.navigate("forgot_password")
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Forgot Password?", color = Color(0xFF4A00E0))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { authViewModel.login(email, password) },
                        enabled = authState.value != AuthState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A00E0))
                    ) {
                        Text("Login", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = { navController.navigate("signup") }) {
                        Text("Don't have an account? Signup", color = Color(0xFF4A00E0))
                    }
                }
            }
        }
    }

}
