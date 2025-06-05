package com.example.iiitbazaar

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.iiitbazaar.model.ChatViewModel
import com.example.iiitbazaar.model.PostViewModel
import com.example.iiitbazaar.pages.ChatListScreen
import com.example.iiitbazaar.pages.ChatScreen
import com.example.iiitbazaar.pages.CreatePostScreen
import com.example.iiitbazaar.pages.EmailVerificationPage
import com.example.iiitbazaar.pages.ForgotPasswordPage
import com.example.iiitbazaar.pages.HomePage
import com.example.iiitbazaar.pages.LoginPage
import com.example.iiitbazaar.pages.PlaceBidScreen
import com.example.iiitbazaar.pages.ProfileScreen
import com.example.iiitbazaar.pages.SignUpPage
import com.example.iiitbazaar.pages.SplashScreen
import com.example.iiitbazaar.pages.UserProfileScreen
import com.example.iiitbazaar.pages.VerifiedPage
import com.example.iiitbazaar.pages.ViewBidsScreen
import com.google.firebase.auth.FirebaseAuth


@Composable
fun MyAppNavigation(currentUserId: String,modifier: Modifier = Modifier, authViewModel: AuthViewModel) {

    val postViewModel: PostViewModel = viewModel()
    val navController = rememberNavController()
    val authStateLive = authViewModel.authState.observeAsState()
    val authState = authStateLive.value


    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.UnAuthenticate -> {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true } // 🔥 clear entire backstack
                }
            }
            is AuthState.Authenticate -> {
                navController.navigate("home") {
                    popUpTo(0) { inclusive = true }
                }
            }
            else -> {}
        }
    }
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController)
        }
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignUpPage(modifier, navController, authViewModel)
        }
        composable("home") {
            HomePage(
                modifier = modifier,
                navController = navController,
                notificationViewModel = hiltViewModel(),
                postViewModel = postViewModel
            )
        }
        composable("verify_email") {
            EmailVerificationPage(modifier, navController, authViewModel)
        }
        composable("verified") {
            VerifiedPage(navController = navController)
        }
        composable("forgot_password") {
            ForgotPasswordPage(navController = navController)
        }
        composable("createPost") {
            CreatePostScreen(navController)
        }


        composable("placeBid/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PlaceBidScreen(postId = postId, currentUserId = currentUserId, navController = navController)
        }

        // Profile screen route with userId and isCurrentUser flag
        composable("profile") {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            ProfileScreen(
                userId = currentUserId,
                isCurrentUser = true,
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("viewBids/{postId}/{postOwnerId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            val postOwnerId = backStackEntry.arguments?.getString("postOwnerId") ?: ""
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            ViewBidsScreen(
                postId = postId,
                currentUserId = currentUserId,
                postOwnerId = postOwnerId,
                navController = navController,
                notificationViewModel = hiltViewModel()
            )
        }


        // chat screen khulega
// Chat List Screen route
        composable("chat_list/{currentUserId}",
            arguments = listOf(navArgument("currentUserId") { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("currentUserId") ?: ""
            ChatListScreen(navController = navController, currentUserId = currentUserId)
        }


        // Add the route for UserProfileScreen
        composable("userProfile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserProfileScreen(userId = userId, navController = navController)
        }

        // go to chatscreen khulega one to one
        composable(
            route = "chatScreen/{chatId}/{chatUserId}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("chatUserId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId").orEmpty()
            val chatUserId = backStackEntry.arguments?.getString("chatUserId").orEmpty()

            val currentUser = FirebaseAuth.getInstance().currentUser
            val currentUserId = currentUser?.uid

            if (currentUserId != null && chatId.isNotEmpty() && chatUserId.isNotEmpty()) {
                val chatViewModel: ChatViewModel = hiltViewModel()

                ChatScreen(
                    navController = navController,
                    chatId = chatId,
                    currentUserId = currentUserId,
                    viewModel = chatViewModel
                )
            } else {
                // Show an error screen or toast
                Text("Something went wrong. Please log in again.")
            }
        }






    }
    }

