package com.example.iotbazar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.iotbazaar.viewmodel.CartViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

        setContent {
            val navController = rememberNavController()
            val cartViewModel: CartViewModel = viewModel()

            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme,  // Ensuring the theme color scheme
                typography = MaterialTheme.typography,    // Ensuring the typography
                shapes = MaterialTheme.shapes            // Ensuring the shape styles
            ) {
                AppNavigation(
                    navController = navController,
                    modifier = Modifier.fillMaxSize(),
                    isLoggedIn = isLoggedIn,
                    cartViewModel = cartViewModel
                )
            }
        }
    }
}
