package com.example.iiitbazaar.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.iiitbazaar.R
import com.example.iiitbazaar.model.Bid
import com.example.iiitbazaar.model.NotificationViewModel
import com.example.iiitbazaar.model.User
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewBidsScreen(
    postId: String,
    currentUserId: String,
    postOwnerId: String,
    navController: NavController,
    notificationViewModel: NotificationViewModel
) {
    val db = FirebaseFirestore.getInstance()
    var bids by remember { mutableStateOf(listOf<Bid>()) }
    var isLoading by remember { mutableStateOf(true) }

    var showDialog by remember { mutableStateOf(false) }
    var selectedBidderId by remember { mutableStateOf<String?>(null) }
    var selectedBidderName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(postId) {
        db.collection("posts")
            .document(postId)
            .collection("bids")
            .get()
            .addOnSuccessListener { result ->
                val bidList = result.mapNotNull { it.toObject(Bid::class.java) }
                    .sortedByDescending { it.bidAmount }
                bids = bidList
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    // Fetch post owner's name (optional for notification)
    var ownerName by remember { mutableStateOf("Someone") }
    LaunchedEffect(postOwnerId) {
        db.collection("users").document(postOwnerId).get().addOnSuccessListener {
            val user = it.toObject(User::class.java)
            ownerName = user?.name ?: "Someone"
        }
    }

    if (showDialog && selectedBidderId != null && selectedBidderName != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Contact ${selectedBidderName}?") },
            text = { Text("Do you want to contact this user?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    val bidderId = selectedBidderId!!
                    navController.navigate("userProfile/$bidderId")

                    notificationViewModel.sendNotification(
                        toUserId = bidderId,
                        message = "🔔 $ownerName wants to contact you!",
                        route = "userProfile/$postOwnerId"
                    )
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bids", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp)
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                bids.isEmpty() -> {
                    Text("No bids placed yet.", style = MaterialTheme.typography.bodyLarge)
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(bids) { bid ->
                            BidItem(
                                bid = bid,
                                isPostOwner = currentUserId == postOwnerId,
                                isHighestBidder = bid == bids.first(),
                                onContactClick = {
                                    selectedBidderId = bid.bidderId
                                    selectedBidderName = bid.bidderName
                                    showDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BidItem(
    bid: Bid,
    isPostOwner: Boolean,
    isHighestBidder: Boolean,
    onContactClick: () -> Unit
) {
    val context = LocalContext.current
    val avatarResId = remember(bid.bidderAvatar) {
        context.resources.getIdentifier(
            bid.bidderAvatar,
            "drawable",
            context.packageName
        ).takeIf { it != 0 } ?: R.drawable.personicon
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = if (isHighestBidder) 4.dp else 2.dp,
        color = if (isHighestBidder) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else MaterialTheme.colorScheme.background
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = avatarResId),
                contentDescription = "Bidder Avatar",
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = bid.bidderName,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isHighestBidder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    if (isHighestBidder) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Highest",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "₹${bid.bidAmount}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isHighestBidder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatBidTime(bid.timestamp.toDate()),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            if (isPostOwner) {
                TextButton(onClick = onContactClick) {
                    Text("Contact")
                }
            }
        }
    }
}

fun formatBidTime(date: Date): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return formatter.format(date)
}
