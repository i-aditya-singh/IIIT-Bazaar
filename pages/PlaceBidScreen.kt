package com.example.iiitbazaar.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.iiitbazaar.model.Bid
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

@Composable
fun PlaceBidScreen(
    postId: String,
    currentUserId: String,
    navController: NavController
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var bidAmount by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var minBidAmount by remember { mutableStateOf(0.0) }

    val user = auth.currentUser

    // Fetch the highest bid or base price
    LaunchedEffect(postId) {
        db.collection("posts").document(postId).get().addOnSuccessListener { postSnapshot ->
            val basePrice = postSnapshot.getDouble("basePrice") ?: 0.0

            db.collection("posts").document(postId).collection("bids")
                .get()
                .addOnSuccessListener { bidsSnapshot ->
                    val highestBid = bidsSnapshot.documents
                        .mapNotNull { it.getDouble("bidAmount") }
                        .maxOrNull()

                    minBidAmount = highestBid?.plus(1.0) ?: basePrice
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter Your Bid", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = bidAmount,
            onValueChange = { bidAmount = it },
            label = { Text("Bid Amount (Min ₹${"%.2f".format(minBidAmount)})") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val amount = bidAmount.toDoubleOrNull()
                if (amount == null || amount < minBidAmount) {
                    Toast.makeText(
                        context,
                        "Bid must be at least ₹${"%.2f".format(minBidAmount)}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                if (user != null) {
                    isSubmitting = true
                    val bidId = UUID.randomUUID().toString()
                    val userDocRef = db.collection("users").document(user.uid)

                    userDocRef.get().addOnSuccessListener { document ->
                        val name = document.getString("name") ?: "Unknown"
                        val avatar = document.getString("profileImageUrl") ?: ""

                        val bid = Bid(
                            bidId = bidId,
                            postId = postId,
                            bidderId = currentUserId,
                            bidderName = name,
                            bidderAvatar = avatar,
                            bidAmount = amount,
                            timestamp = Timestamp.now()
                        )

                        db.collection("posts")
                            .document(postId)
                            .collection("bids")
                            .document(bidId)
                            .set(bid)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Bid placed!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to place bid", Toast.LENGTH_SHORT).show()
                                isSubmitting = false
                            }
                    }
                }
            },
            enabled = !isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Bid")
        }
    }
}
