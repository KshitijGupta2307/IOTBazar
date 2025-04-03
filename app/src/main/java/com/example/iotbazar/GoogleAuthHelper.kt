package com.example.iotbazar

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.navigation.NavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthHelper(
    private val oneTapClient: SignInClient,
    private val context: Context
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId("689525090024-iugvhseb5i332l5rh7k9d38996oslp7g.apps.googleusercontent.com") // Replace with Firebase Web Client ID
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

    fun beginSignIn(onSuccess: (PendingIntent) -> Unit) {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                onSuccess(result.pendingIntent)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleSignIn", "Sign-In Failed: ${e.localizedMessage}", e)
                Toast.makeText(context, "Google Sign-In Failed!", Toast.LENGTH_SHORT).show()
            }
    }

    fun handleSignInResult(result: ActivityResult, navController: NavController) {
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            try {
                val credential: SignInCredential =
                    oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken

                if (idToken != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true } // Removes login from back stack
                                }
                            } else {
                                Log.e("GoogleSignIn", "Firebase Authentication Failed", task.exception)
                                Toast.makeText(context, "Authentication Failed!", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Log.e("GoogleSignIn", "ID Token is null")
                    Toast.makeText(context, "Google Sign-In Failed!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("GoogleSignIn", "Sign-in handling failed", e)
                Toast.makeText(context, "Google Sign-In Failed!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Sign-in Canceled", Toast.LENGTH_SHORT).show()
        }
    }
}
