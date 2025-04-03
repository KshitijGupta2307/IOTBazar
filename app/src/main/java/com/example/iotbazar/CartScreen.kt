package com.example.iotbazaar.ui.screens.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.iotbazaar.viewmodel.CartItem
import com.example.iotbazaar.viewmodel.CartViewModel

import com.example.iotbazar.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavHostController, cartViewModel: CartViewModel) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalPrice = cartItems.sumOf { it.product.price * it.quantity }
    val isCartEmpty = cartItems.isEmpty()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Your Cart") }) },
        bottomBar = {
            Column {
                BottomNavigationWithCheckout(navController, totalPrice, !isCartEmpty)
                BottomNavBar(
                    navController,
                    currentRoute = ""
                ) // ✅ Fixed closing issue
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = if (isCartEmpty) Alignment.Center else Alignment.TopStart
        ) {
            if (isCartEmpty) {
                EmptyCartUI(navController)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cartItems) { cartItem ->
                        CartItemRow(cartItem, cartViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationWithCheckout(navController: NavHostController, totalPrice: Double, isNotEmpty: Boolean) {
    if (isNotEmpty) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total: ₹${String.format("%.2f", totalPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Button(onClick = { navController.navigate("checkout") }) {
                    Text("Checkout")
                }
            }
        }
    }
}

@Composable
fun CartItemRow(cartItem: CartItem, cartViewModel: CartViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ Image Display Fixed
            AsyncImage(
                model = cartItem.product.imageUrl,
                contentDescription = cartItem.product.name,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(cartItem.product.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "₹${cartItem.product.price} x ${cartItem.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // ✅ Quantity Controls Fixed
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { cartViewModel.decreaseQuantity(cartItem.product) }) {
                    Text("-", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Text(cartItem.quantity.toString(), style = MaterialTheme.typography.bodyMedium)
                IconButton(onClick = { cartViewModel.addToCart(cartItem.product) }) {
                    Text("+", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { cartViewModel.removeFromCart(cartItem.product) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun EmptyCartUI(navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Your cart is empty!", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate("home") }) { Text("Start Shopping") }
    }
}
