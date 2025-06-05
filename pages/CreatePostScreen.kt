package com.example.iiitbazaar.pages

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.iiitbazaar.model.CreatePostViewModel
import com.example.iiitbazaar.utils.uploadImageToImgur
import com.google.firebase.Timestamp
import java.util.Date
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavController,
    viewModel: CreatePostViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var description by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    var enableBidding by remember { mutableStateOf(false) }
    var basePrice by remember { mutableStateOf("") }

    var customTimeValue by remember { mutableStateOf("") }
    var customTimeUnit by remember { mutableStateOf("Minutes") }
    val timeUnits = listOf("Minutes", "Hours", "Days")

    var unitExpanded by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
        imageBitmap = null
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            imageBitmap = it
            selectedImageUri = null
        } ?: Toast.makeText(context, "Camera capture failed", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Post", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF8E24AA),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var expanded by remember { mutableStateOf(false) }

            Box {
                Button(
                    onClick = { expanded = true },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3949AB))
                ) {
                    Text("Upload Image", color = Color.White)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Gallery") },
                        onClick = {
                            expanded = false
                            imagePickerLauncher.launch("image/*")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Camera") },
                        onClick = {
                            expanded = false
                            cameraLauncher.launch(null)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                imageBitmap != null -> Image(
                    bitmap = imageBitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                selectedImageUri != null -> Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = enableBidding, onCheckedChange = { enableBidding = it })
                Text("Enable Bidding")
            }

            if (enableBidding) {
                OutlinedTextField(
                    value = basePrice,
                    onValueChange = { basePrice = it },
                    label = { Text("Base Price (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = customTimeValue,
                        onValueChange = { customTimeValue = it },
                        label = { Text("Duration") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = unitExpanded,
                        onExpandedChange = { unitExpanded = !unitExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = customTimeUnit,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unit") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = unitExpanded,
                            onDismissRequest = { unitExpanded = false }
                        ) {
                            timeUnits.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(unit) },
                                    onClick = {
                                        customTimeUnit = unit
                                        unitExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (description.isBlank()) return@Button

                    isUploading = true

                    val value = customTimeValue.toLongOrNull() ?: 0L
                    val durationMillis = if (enableBidding && value > 0) {
                        when (customTimeUnit) {
                            "Minutes" -> TimeUnit.MINUTES.toMillis(value)
                            "Hours" -> TimeUnit.HOURS.toMillis(value)
                            "Days" -> TimeUnit.DAYS.toMillis(value)
                            else -> 0L
                        }
                    } else 0L

                    val biddingEndTime = if (enableBidding && durationMillis > 0)
                        Timestamp(Date(Date().time + durationMillis))

                    else null

                    val handleUploadResult: (String?) -> Unit = { url ->
                        isUploading = false
                        if (url != null) {
                            viewModel.createPost(
                                imageUrl = url,
                                description = description,
                                isBiddingEnabled = enableBidding,
                                basePrice = basePrice.toDoubleOrNull() ?: 0.0,
                                biddingEndTime = biddingEndTime,
                                onSuccess = { navController.popBackStack() },
                                onFailure = {
                                    Toast.makeText(context, "Post upload failed", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    when {
                        imageBitmap != null -> uploadImageToImgur(context, imageBitmap!!, handleUploadResult)
                        selectedImageUri != null -> uploadImageToImgur(context, selectedImageUri!!, handleUploadResult)
                        else -> {
                            isUploading = false
                            Toast.makeText(context, "Please select or capture an image.", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = !isUploading,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E24AA)),
                shape = RoundedCornerShape(50)
            ) {
                Text(if (isUploading) "Uploading..." else "Post", color = Color.White)
            }
        }
    }
}

