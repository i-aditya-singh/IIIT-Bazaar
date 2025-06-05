package com.example.iiitbazaar.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.iiitbazaar.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.iiitbazaar.R

@Composable
fun UserProfileScreen(
    userId: String,
    navController: NavController
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val userState = remember { mutableStateOf<User?>(null) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        if (userId == currentUserId) {
            navController.navigate("profile") {
                popUpTo("userProfile/$userId") { inclusive = true }
            }
        } else {
            val userSnap = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .await()
            userState.value = userSnap.toObject(User::class.java)
            isLoading.value = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading.value) {
            CircularProgressIndicator()
        } else {
            userState.value?.let { user ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(24.dp)
                ) {
                    // Profile image
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .shadow(10.dp, CircleShape)
                    ) {
                        val painter = if (user.avatar != null && user.avatar.startsWith("http")) {
                            rememberAsyncImagePainter(user.avatar)
                        } else {
                            val context = LocalContext.current
                            val resId = remember(user.avatar) {
                                context.resources.getIdentifier(user.avatar, "drawable", context.packageName)
                            }
                            painterResource(id = resId.takeIf { it != 0 } ?: R.drawable.personicon)
                        }

                        Image(
                            painter = painter,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Name
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Chat Button
                    Button(
                        onClick = {
                            val safeCurrentUserId = currentUserId ?: return@Button // or handle error appropriately

                            val chatId = if (safeCurrentUserId < user.uid) {
                                "${safeCurrentUserId}_${user.uid}"
                            } else {
                                "${user.uid}_$safeCurrentUserId"
                            }

                            navController.navigate("chatScreen/$chatId/${user.uid}")

                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(50.dp)
                    ) {
                        Text("Chat", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } ?: Text(
                text = "User not found",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
