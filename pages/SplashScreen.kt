package com.example.iiitbazaar.pages

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.iiitbazaar.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0.5f) }
    val offsetY = remember { Animatable(30f) }

    val pulseAnim = rememberInfiniteTransition()
    val pulseScale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Start animation and auth check
    LaunchedEffect(true) {
        scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        offsetY.animateTo(0f, animationSpec = tween(900))

        delay(3000)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && user.isEmailVerified) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D47A1), Color(0xFF64B5F6))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Bubble/Star field background
        for (i in 0..20) {
            FloatingCircle(
                size = Random.nextInt(8, 20).dp,
                duration = Random.nextInt(3000, 6000),
                offsetX = Random.nextInt(-150, 150).dp,
                startDelay = i * 150L
            )
        }

        // Glowing Logo Centered
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset(y = offsetY.value.dp)
                .scale(pulseScale)
        ) {
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .graphicsLayer {
                        alpha = 0.3f
                        scaleX = 1.4f
                        scaleY = 1.4f
                    }
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )

            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale.value)
            )
        }
    }
}

@Composable
fun FloatingCircle(
    size: Dp,
    duration: Int,
    offsetX: Dp,
    startDelay: Long
) {
    val anim = rememberInfiniteTransition()
    val yOffset by anim.animateFloat(
        initialValue = 1000f,
        targetValue = -100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, delayMillis = startDelay.toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = yOffset.dp)
            .size(size)
            .background(Color.White.copy(alpha = 0.15f), shape = CircleShape)
    )
}
