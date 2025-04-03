package com.example.iotbazar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.iotbazaar.ui.screens.cart.CartScreen
import com.example.iotbazaar.ui.screens.home.HomeScreen
import com.example.iotbazaar.viewmodel.CartViewModel
import com.example.iotbazar.LoginScreen
import com.example.iotbazar.ProfileScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier,
    isLoggedIn: Boolean,
    cartViewModel: CartViewModel  // ✅ Pass a shared instance
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "home" else "login",
        modifier = modifier
    ) {
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController, cartViewModel) }  // ✅ Use shared ViewModel
        composable("cart") { CartScreen(navController, cartViewModel) }  // ✅ Use shared ViewModel
        composable("profile") { ProfileScreen(navController) }
    }
}
