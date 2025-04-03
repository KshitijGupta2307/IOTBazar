package com.example.iotbazar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    // Track authentication state dynamically
    var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }

    // Firebase Auth Listener to update login state
    LaunchedEffect(Unit) {
        auth.addAuthStateListener {
            isLoggedIn = it.currentUser != null
        }
    }

    Scaffold { paddingValues ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            isLoggedIn = isLoggedIn // ✅ Pass 'isLoggedIn' properly
        )
    }
}
