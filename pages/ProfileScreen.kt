@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.iiitbazaar.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.iiitbazaar.AuthViewModel
import com.example.iiitbazaar.R
import com.example.iiitbazaar.model.ProfileViewModel
import com.example.iiitbazaar.ui.theme.Poppins
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    userId: String,
    isCurrentUser: Boolean,
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel
) {
    val userProfile by profileViewModel.userProfile.observeAsState()
    val isLoading by profileViewModel.isLoading.observeAsState(false)
    val error by profileViewModel.error.observeAsState()
    val authState by authViewModel.authState.observeAsState()

    var showAvatarDialog by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }

    val context = LocalContext.current





    LaunchedEffect(userId) {
        profileViewModel.fetchUserProfile(userId)
    }

    Scaffold(containerColor = Color.Transparent) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF4A148C), Color(0xFF6A1B9A), Color(0xFF8E24AA))
                    )
                )
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator(color = Color.White)

                error != null -> Text("Error: $error", color = Color.White)

                userProfile != null -> {
                    val user = userProfile!!

                    Column(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val avatarId = when (user.avatar) {
                            "boy1" -> R.drawable.boy1
                            "boy2" -> R.drawable.boy2
                            "girl1" -> R.drawable.girl1
                            "girl2" -> R.drawable.girl2
                            else -> R.drawable.personicon
                        }

                        Card(
                            modifier = Modifier.padding(16.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(modifier = Modifier.size(120.dp)) {
                                    Image(
                                        painter = painterResource(id = avatarId),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(CircleShape)
                                            .align(Alignment.Center)
                                    )

                                    if (isCurrentUser) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.edit_icon),
                                            contentDescription = "Edit Avatar",
                                            modifier = Modifier
                                                .size(28.dp)
                                                .align(Alignment.BottomEnd)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                                .clickable { showAvatarDialog = true }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Name: ${user.name}", style = MaterialTheme.typography.titleMedium)
                                Text("Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (isCurrentUser) {
                            StyledActionButton("Change Name") {
                                newName = user.name
                                showNameDialog = true
                            }

                            StyledActionButton("Change Password") {
                                showPasswordDialog = true
                            }

                            var showDialog by remember { mutableStateOf(false) }

                            SignOutConfirmationDialog(
                                showDialog = showDialog,
                                onConfirm = {
                                    showDialog = false
                                  authViewModel.signout()
                                },
                                onDismiss = {
                                    showDialog = false
                                }
                            )

                            StyledActionButton("Sign Out") {
                                showDialog = true
                            }
                        }

                        if (showAvatarDialog) {
                            AvatarSelectionDialog(
                                onDismiss = { showAvatarDialog = false },
                                onAvatarSelected = { selectedAvatar ->
                                    profileViewModel.updateAvatar(userId, selectedAvatar)
                                    showAvatarDialog = false
                                }
                            )
                        }

                        if (showNameDialog) {
                            NameChangeDialog(
                                currentName = newName,
                                onDismiss = { showNameDialog = false },
                                onSave = {
                                    profileViewModel.updateName(userId, it)
                                    Toast.makeText(context, "Name updated", Toast.LENGTH_SHORT).show()
                                    showNameDialog = false
                                }
                            )
                        }

                        if (showPasswordDialog) {
                            PasswordChangeDialog(
                                onDismiss = { showPasswordDialog = false },
                                onSave = { oldPass, newPass ->
                                    val userFirebase = FirebaseAuth.getInstance().currentUser
                                    val email = userFirebase?.email

                                    if (email != null && oldPass.isNotBlank() && newPass.length >= 6) {
                                        val credential = EmailAuthProvider.getCredential(email, oldPass)
                                        userFirebase.reauthenticate(credential)
                                            .addOnSuccessListener {
                                                userFirebase.updatePassword(newPass)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(
                                                            context, "Password updated", Toast.LENGTH_SHORT
                                                        ).show()
                                                        showPasswordDialog = false
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(
                                                            context, "Failed to update password", Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(
                                                    context, "Re-authentication failed", Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    } else {
                                        Toast.makeText(
                                            context, "Invalid input", Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        }
                    }
                }

                else -> Text("No user data found.", color = Color.White)
            }
        }
    }
}



@Composable
fun AvatarSelectionDialog(
    onDismiss: () -> Unit,
    onAvatarSelected: (String) -> Unit
) {
    val avatarOptions = listOf("boy1", "boy2", "girl1", "girl2")
    val avatarResources = mapOf(
        "boy1" to R.drawable.boy1,
        "boy2" to R.drawable.boy2,
        "girl1" to R.drawable.girl1,
        "girl2" to R.drawable.girl2
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Avatar") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    avatarOptions.forEach { avatarKey ->
                        Image(
                            painter = painterResource(id = avatarResources[avatarKey]!!),
                            contentDescription = avatarKey,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .clickable { onAvatarSelected(avatarKey) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun StyledActionButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFBA68C8), Color(0xFF9C27B0))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins,
                color = Color.White
            )
        }
    }
}




@Composable
fun NameChangeDialog(currentName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Name") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("New Name") }
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onSave(name) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PasswordChangeDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Current Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                )
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(if (passwordVisible) "Hide Password" else "Show Password")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (oldPassword.isNotBlank() && newPassword.length >= 6) {
                    onSave(oldPassword, newPassword)
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun SignOutConfirmationDialog(
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}




