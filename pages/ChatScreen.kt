package com.example.iiitbazaar.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.iiitbazaar.model.ChatViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    chatId: String,
    currentUserId: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val chatUser by viewModel.chatUser.collectAsState()
    val context = LocalContext.current
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(chatId, currentUserId) {
        viewModel.loadMessages(chatId)
        viewModel.fetchUsers(currentUserId, chatId.replace(currentUserId, "").replace("_", ""))
    }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(0)
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFEDE7F6), Color(0xFFF3E5F5), Color.White)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val avatarResId = remember(chatUser?.avatar) {
                            chatUser?.avatar?.let {
                                context.resources.getIdentifier(it, "drawable", context.packageName)
                            }
                        }

                        if (avatarResId != null && avatarResId != 0) {
                            Image(
                                painter = painterResource(id = avatarResId),
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Text(chatUser?.name ?: "Chat")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding() // only this is enough when root activity is correct
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    reverseLayout = true
                ) {
                    items(messages.reversed()) { msg ->
                        val isCurrentUser = msg.senderId == currentUserId

                        Row(
                            horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            if (!isCurrentUser) {
                                val avatarResId = remember(chatUser?.avatar) {
                                    chatUser?.avatar?.let {
                                        context.resources.getIdentifier(it, "drawable", context.packageName)
                                    }
                                }

                                if (avatarResId != null && avatarResId != 0) {
                                    Image(
                                        painter = painterResource(id = avatarResId),
                                        contentDescription = "Avatar",
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isCurrentUser) Color(0xFFDCF8C6) else Color.White,
                                tonalElevation = 2.dp,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }

                // ✅ Message Input stays above the keyboard
                Surface(
                    tonalElevation = 4.dp,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            placeholder = { Text("Type a message...") },
                            shape = RoundedCornerShape(24.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF1F1F1),
                                focusedContainerColor = Color(0xFFF1F1F1),
                                disabledContainerColor = Color(0xFFF1F1F1)
                            )
                        )
                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    viewModel.sendMessage(chatId, currentUserId, messageText.trim())
                                    messageText = ""
                                }
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                        }
                    }
                }
            }
        }
    }
}

