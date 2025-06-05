package com.example.iiitbazaar.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.iiitbazaar.R

@Composable
fun VerifiedPage(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // verification image
        Image(
            painter = painterResource(id = R.drawable.emailverfication),
            contentDescription = "Verified",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(200.dp)
        )


        Spacer(modifier = Modifier.height(20.dp))

        // text for verification
        Text("Your email verified successfully", fontSize = 22.sp)
        Spacer(modifier = Modifier.height(20.dp))

        // homepage pe wapas jaane wala button
        Button(onClick = {
            navController.navigate("home") {
                popUpTo("verified") { inclusive = true }
            }
        }) {
            Text("Home Screen")
        }
    }
}
