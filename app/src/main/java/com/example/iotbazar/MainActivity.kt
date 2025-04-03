package com.example.iotbazar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.iotbazaar.viewmodel.CartViewModel
import com.example.iotbazar.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val cartViewModel: CartViewModel = viewModel()  // ✅ Shared ViewModel instance

            AppNavigation(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                isLoggedIn = true,  // Replace with actual login check
                cartViewModel = cartViewModel  // ✅ Pass the shared ViewModel
            )
        }
    }
}