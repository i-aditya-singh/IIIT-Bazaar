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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.iiitbazaar.R
import com.example.iiitbazaar.model.CreatePostViewModel
import com.example.iiitbazaar.model.HomeViewModel
import com.example.iiitbazaar.model.NotificationViewModel
import com.example.iiitbazaar.model.Post
import com.example.iiitbazaar.model.PostViewModel
import com.example.iiitbazaar.model.ProfileViewModel
import com.example.iiitbazaar.util.BiddingNotificationManager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    notificationViewModel: NotificationViewModel,
    postViewModel: PostViewModel
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val username by homeViewModel.userName
    val userProfile by profileViewModel.userProfile.observeAsState()
    val createPostViewModel: CreatePostViewModel = hiltViewModel()

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val currentUserId = currentUser?.uid ?: ""

    val postList by postViewModel.posts.collectAsState()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val isRefreshing = remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    val notificationList by notificationViewModel.notifications.observeAsState(emptyList())

    // Fetch user profile
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            profileViewModel.fetchUserProfile(currentUserId)
            notificationViewModel.startListening(currentUserId)
        }
    }

    val filteredPosts = postList.filter {
        it.description.contains(searchQuery.text, ignoreCase = true) ||
                it.username.contains(searchQuery.text, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IIIT Bazaar", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    // Sell Button
                    Button(
                        onClick = { navController.navigate("createPost") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8E24AA),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .padding(end = 8.dp)
                    ) {
                        Text("Sell", fontWeight = FontWeight.Bold)
                    }

                    // Notification Bell with Dropdown
                    Box {
                        IconButton(onClick = { showNotifications = !showNotifications }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }

                        DropdownMenu(
                            expanded = showNotifications,
                            onDismissRequest = { showNotifications = false }
                        ) {
                            if (notificationList.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No notifications") },
                                    onClick = { showNotifications = false }
                                )
                            } else {
                                notificationList.forEach { notification ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(notification.message)
                                                if (!notification.isRead) {
                                                    Text("Mark as Read", color = Color.Gray, fontSize = 10.sp)
                                                }
                                            }
                                        },
                                        onClick = {
                                            showNotifications = false
                                            navController.navigate(notification.route)
                                            if (!notification.isRead) {
                                                notificationViewModel.markAsRead(currentUserId, notification.id)
                                            }
                                        }
                                    )
                                }

                                Divider()

                                DropdownMenuItem(
                                    text = { Text("🧹 Clear All", color = Color.Red) },
                                    onClick = {
                                        showNotifications = false
                                        notificationViewModel.clearAllNotifications(currentUserId)
                                    }
                                )
                            }
                        }
                    }

                    // Profile Avatar and Name
                    val avatar = userProfile?.avatar ?: "default"
                    val avatarResId = remember(avatar) {
                        context.resources.getIdentifier(avatar, "drawable", context.packageName)
                    }
                    val firstName = userProfile?.name?.split(" ")?.firstOrNull() ?: ""

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .width(40.dp)
                    ) {
                        IconButton(onClick = { navController.navigate("profile") }) {
                            if (avatarResId != 0) {
                                Image(
                                    painter = painterResource(id = avatarResId),
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Text(
                            text = firstName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        if (showChatDialog) {
            AlertDialog(
                onDismissRequest = { showChatDialog = false },
                title = { Text("Chat Options", fontWeight = FontWeight.Bold) },
                text = { Text("Want to open your chats?") },
                confirmButton = {
                    TextButton(onClick = {
                        showChatDialog = false
                        navController.navigate("chat_list/$currentUserId")
                    }) {
                        Text("Open Chat")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showChatDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFBBDEFB), Color(0xFFE1BEE7))
                    )
                )
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search posts...") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFCE93D8),
                    focusedBorderColor = Color(0xFF8E24AA),
                    cursorColor = Color(0xFF8E24AA),
                    focusedContainerColor = Color(0xFFF3E5F5),
                    unfocusedContainerColor = Color(0xFFF3E5F5)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing.value),
                onRefresh = {
                    isRefreshing.value = true
                    postViewModel.fetchPosts {
                        isRefreshing.value = false
                    }
                }
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredPosts) { post ->
                        PostCard(
                            post = post,
                            navController = navController,
                            currentUserId = currentUserId,
                            notificationViewModel = notificationViewModel,
                            createPostViewModel = createPostViewModel
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PostCard(
    post: Post,
    navController: NavController,
    currentUserId: String,
    notificationViewModel: NotificationViewModel,
    createPostViewModel: CreatePostViewModel
) {
    val biddingNotificationManager = remember { BiddingNotificationManager(notificationViewModel)}
    var showImageDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFCE4EC), Color(0xFFE1BEE7))
    )

    val remainingTime = remember { mutableStateOf("") }

    LaunchedEffect(post.biddingEndTime) {
        while (true) {
            val endTime = post.biddingEndTime?.toDate()?.time ?: 0L
            val currentTime = System.currentTimeMillis()
            val millisLeft = endTime - currentTime

            remainingTime.value = if (millisLeft <= 0) {
                "⏰ Bidding Ended"
            } else {
                val h = TimeUnit.MILLISECONDS.toHours(millisLeft)
                val m = TimeUnit.MILLISECONDS.toMinutes(millisLeft) % 60
                val s = TimeUnit.MILLISECONDS.toSeconds(millisLeft) % 60
                String.format("⏰ %02d:%02d:%02d left", h, m, s)
            }

            if (millisLeft <= 0) {
                // 🟡 Trigger notifications
                biddingNotificationManager.checkAndSendNotifications(post)
                break
            }

            delay(1000)
        }
    }



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.background(gradientBrush).padding(16.dp)) {

            // Profile + Options
            Row(verticalAlignment = Alignment.CenterVertically) {
                val painter = if (post.profileImageUrl.startsWith("http")) {
                    rememberAsyncImagePainter(post.profileImageUrl)
                } else {
                    val resId = remember(post.profileImageUrl) {
                        context.resources.getIdentifier(
                            post.profileImageUrl, "drawable", context.packageName
                        )
                    }
                    painterResource(id = if (resId != 0) resId else R.drawable.personicon)
                }

                Image(
                    painter = painter,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.username,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A148C)
                        )
                    )
                    Text(
                        text = formatSmartTimestamp(post.timestamp.toDate().time),
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                    )
                }

                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        val biddingActive = (post.biddingEndTime?.toDate()?.time ?: 0L) > System.currentTimeMillis()

                        if (post.isBiddingEnabled == true) {
                            if (post.userId != currentUserId && biddingActive) {
                                DropdownMenuItem(
                                    text = { Text("Place a Bid") },
                                    onClick = {
                                        expanded = false
                                        navController.navigate("placeBid/${post.postId}")
                                    }
                                )
                            }

                            DropdownMenuItem(
                                text = { Text("View Bidding") },
                                onClick = {
                                    expanded = false
                                    navController.navigate("viewBids/${post.postId}/${post.userId}")

                                }
                            )
                        }

                        if (post.userId == currentUserId) {
                            DropdownMenuItem(
                                text = { Text("Delete Post", color = Color.Red) },
                                onClick = {
                                    expanded = false
                                    createPostViewModel.deletePost(
                                        post.postId,
                                        onSuccess = {
                                            Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT).show()
                                        },
                                        onFailure = {
                                            Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Image
            if (post.imageUrl.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(post.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .clickable { showImageDialog = true }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Description
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4A148C)
                )
            )

            // Bidding Info (placed below description)
            if (post.isBiddingEnabled == true) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🪙 Base Price: ₹${post.basePrice}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color(0xFF6A1B9A),
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = remainingTime.value,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (remainingTime.value.contains("Ended")) Color.Red else Color(0xFF00897B),
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }

    if (showImageDialog) {
        Dialog(onDismissRequest = { showImageDialog = false }) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(post.imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.95f)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { showImageDialog = false }
                )
            }
        }
    }
}


fun formatSmartTimestamp(timestamp: Long): String {
    val postDate = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }
    val now = Calendar.getInstance()
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val fullDateFormat = SimpleDateFormat("dd MMMM yyyy • hh:mm a", Locale.getDefault())
    return when {
        isSameDay(postDate, now) -> "Today • ${timeFormat.format(postDate.time)}"
        isYesterday(postDate, now) -> "Yesterday • ${timeFormat.format(postDate.time)}"
        else -> fullDateFormat.format(postDate.time)
    }
}

fun isSameDay(c1: Calendar, c2: Calendar): Boolean {
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}

fun isYesterday(post: Calendar, now: Calendar): Boolean {
    val yesterday = Calendar.getInstance().apply {
        timeInMillis = now.timeInMillis
        add(Calendar.DAY_OF_YEAR, -1)
    }
    return isSameDay(post, yesterday)
}
