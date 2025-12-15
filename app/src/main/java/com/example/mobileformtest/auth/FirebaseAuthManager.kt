package com.example.mobileformtest.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Lightweight wrapper around [FirebaseAuth] so the rest of the app does not touch the
 * Firebase SDK directly. Exposes auth state as a StateFlow so Compose UI can react to it.
 */
class FirebaseAuthManager(
    private val firebaseAuth: FirebaseAuth = Firebase.auth
) {

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _authState.value = auth.currentUser
    }

    private val _authState = MutableStateFlow(firebaseAuth.currentUser)
    val authState: StateFlow<FirebaseUser?> = _authState.asStateFlow()

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    fun isSignedIn(): Boolean = firebaseAuth.currentUser != null

    fun signInWithEmail(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password)

    fun registerWithEmail(email: String, password: String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password)

    fun signInWithCredential(credential: AuthCredential) =
        firebaseAuth.signInWithCredential(credential)

    fun sendPasswordReset(email: String) =
        firebaseAuth.sendPasswordResetEmail(email)

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun dispose() {
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}
