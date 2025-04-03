package com.example.iotbazaar.ui.screens.cart

import androidx.compose.foundation.background
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
    val isCartEmpty = cartItems.isEmpty()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                actions = {
                    if (!isCartEmpty) {
                        IconButton(onClick = { cartViewModel.clearCart() }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Clear Cart", tint = Color.White)
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column {
                if (!isCartEmpty) CheckoutBar(cartItems) { showDialog = true }
                BottomNavBar(navController, "cart")
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
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { cartItem ->
                        CartItemRow(cartItem, cartViewModel)
                    }
                }
            }
        }
    }

    // Show checkout dialog when the button is clicked
    if (showDialog) {
        CheckoutDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, phone, whatsapp, address, email ->
                showDialog = false
                navController.navigate("orderConfirmation") // Proceed to order confirmation
            }
        )
    }
}

@Composable
fun CheckoutBar(cartItems: List<CartItem>, onCheckoutClick: () -> Unit) {
    val totalPrice = cartItems.sumOf { it.product.price * it.quantity }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp
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
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Button(
                onClick = onCheckoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Checkout")
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
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = cartItem.product.imageUrl,
                contentDescription = cartItem.product.name,
                modifier = Modifier
                    .size(90.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = cartItem.product.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = "₹${cartItem.product.price} x ${cartItem.quantity}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = { cartViewModel.decreaseQuantity(cartItem.product) },
                        enabled = cartItem.quantity > 1,
                        contentPadding = PaddingValues(4.dp),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("-", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(text = cartItem.quantity.toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { cartViewModel.addToCart(cartItem.product) },
                        contentPadding = PaddingValues(4.dp),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("+", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { cartViewModel.removeFromCart(cartItem.product) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun EmptyCartUI(navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Your cart is empty!", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("home") },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Start Shopping")
        }
    }
}

@Composable
fun CheckoutDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Shipment Details", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CheckoutTextField(value = name, label = "Name") { name = it }
                CheckoutTextField(value = phone, label = "Phone Number") { phone = it }
                CheckoutTextField(value = whatsapp, label = "WhatsApp Number") { whatsapp = it }
                CheckoutTextField(value = address, label = "Address", maxLines = 2) { address = it }
                CheckoutTextField(value = email, label = "Email ID") { email = it }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty()) {
                        onConfirm(name, phone, whatsapp, address, email)
                    }
                },
                enabled = name.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty()
            ) {
                Text("Proceed to payment ")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CheckoutTextField(value: String, label: String, maxLines: Int = 1, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = maxLines == 1,
        maxLines = maxLines
    )
}
