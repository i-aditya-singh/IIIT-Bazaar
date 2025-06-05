package com.example.iiitbazaar


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.iiitbazaar.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }


    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.UnAuthenticate
        } else {
            _authState.value = AuthState.Authenticate
        }
    }

    fun login(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticate
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signup(name: String, email: String, password: String) {
        if (name.isEmpty()) {
            _authState.value = AuthState.Error("Name can't be empty")
            return
        }
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    val uid = currentUser?.uid

                    if (uid != null) {

                        val defaultAvatar = "default"

                        val user = User(
                            uid = uid,
                            name = name,
                            email = email,
                            avatar = defaultAvatar
                        )

                        FirebaseFirestore.getInstance().collection("users")
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener {
                                currentUser.sendEmailVerification()
                                    .addOnSuccessListener {
                                        _authState.value = AuthState.EmailVerificationSent
                                    }
                                    .addOnFailureListener {
                                        _authState.value = AuthState.Error("Failed to send verification email: ${it.message}")
                                    }
                            }
                            .addOnFailureListener {
                                _authState.value = AuthState.Error("Failed to save user data: ${it.message}")
                            }
                    } else {
                        _authState.value = AuthState.Error("User ID not found")
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Something went wrong"
                    _authState.value = AuthState.Error(errorMessage)
                }
            }
    }



    fun resendVerificationEmail() {
        val user = auth.currentUser
        if (user != null && !user.isEmailVerified) {
            user.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Info("Verification email resent")
                } else {
                    _authState.value = AuthState.Error("Failed to resend email")
                }
            }
        }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.UnAuthenticate
    }

    fun getUserProfile(uid: String, onResult: (User?) -> Unit) {
        FirebaseFirestore.getInstance().collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val user = doc.toObject(User::class.java)
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }


}


sealed class AuthState {
    object Authenticate : AuthState()
    object UnAuthenticate : AuthState()
    object Loading : AuthState()
    object EmailNotVerified : AuthState()
    object EmailVerificationSent : AuthState()
    data class Info(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
