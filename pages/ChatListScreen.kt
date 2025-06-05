@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.iiitbazaar.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.iiitbazaar.model.ChatListViewModel
import com.example.iiitbazaar.model.User

@Composable
fun ChatListScreen(
    navController: NavController,
    currentUserId: String,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val chatList by viewModel.chatUsers.collectAsState()

    LaunchedEffect(currentUserId) {
        viewModel.fetchChatUsers(currentUserId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chats") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (chatList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No chats yet", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                items(chatList) { user ->
                    ChatUserItem(user) {
                        navController.navigate("chatScreen/${user.uid}")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatUserItem(user: User, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val context = LocalContext.current
        if (!user.avatar.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(model = user.avatar),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 12.dp)
            )
        }
        Column {
            Text(text = user.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Tap to chat", style = MaterialTheme.typography.bodySmall)
        }
    }
}

