package com.example.iotbazar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.iotbazaar.viewmodel.CartViewModel
import com.example.iotbazar.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme { // ✅ Apply the app theme
                val navController = rememberNavController() // ✅ Navigation Controller
                val cartViewModel: CartViewModel = viewModel() // ✅ CartViewModel

                // ✅ Use AppNavigation for modular navigation
                AppNavigation(navController, modifier = androidx.compose.ui.Modifier, isLoggedIn = true)
            }
        }
    }
}
