package com.example.iiitbazaar.pages

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.iiitbazaar.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EmailVerificationPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var canResend by remember { mutableStateOf(false) }
    var emailVerified by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(60) }
    val coroutineScope = rememberCoroutineScope()

    // Start 60s countdown on first load
    LaunchedEffect(Unit) {
        for (i in 60 downTo 1) {
            countdown = i
            delay(1000)
        }
        canResend = true
    }

    // Periodically check email verification
    LaunchedEffect(Unit) {
        while (!emailVerified) {
            delay(3000)
            FirebaseAuth.getInstance().currentUser?.reload()
            emailVerified = FirebaseAuth.getInstance().currentUser?.isEmailVerified ?: false
        }

        Toast.makeText(context, "Please Wait!", Toast.LENGTH_SHORT).show()
        navController.navigate("verified") {                //navigate to verified page
            popUpTo("verify_email") { inclusive = true }   //inclusive = true means it will remove this screen from backstack
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Please verify your email address.", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("We’ve sent a verification email to your inbox.")
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                authViewModel.resendVerificationEmail()
                Toast.makeText(context, "Verification email sent again.", Toast.LENGTH_SHORT).show()
                canResend = false

                coroutineScope.launch {
                    for (i in 60 downTo 1) {
                        countdown = i
                        delay(1000)
                    }
                    canResend = true
                }
            },
            enabled = canResend
        ) {
            Text("Resend Verification Email")
        }

        if (!canResend) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Resend after $countdown seconds")
        }
    }
}
