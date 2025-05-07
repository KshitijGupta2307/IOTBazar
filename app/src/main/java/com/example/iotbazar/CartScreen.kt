package com.example.iotbazaar.ui.screens.cart

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.iotbazaar.Constants.BASE_URL
import com.example.iotbazaar.viewmodel.CartItem
import com.example.iotbazaar.viewmodel.CartViewModel
import com.example.iotbazar.BottomNavBar
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavHostController, cartViewModel: CartViewModel) {
    val context = LocalContext.current
    val cartItems by cartViewModel.cartItems.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(onClick = { cartViewModel.clearCart() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear Cart", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column {
                if (cartItems.isNotEmpty()) {
                    CheckoutBar(cartItems) { showDialog = true }
                }
                BottomNavBar(navController, "cart")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = if (cartItems.isEmpty()) Alignment.Center else Alignment.TopStart
        ) {
            if (cartItems.isEmpty()) {
                EmptyCartUI(navController)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemRow(item, cartViewModel)
                    }
                }
            }
        }
    }

    if (showDialog) {
        CheckoutDialog(
            cartItems = cartItems,
            onDismiss = { showDialog = false },
            onConfirm = { name, phone, address, email ->
                showDialog = false
                val totalAmount = cartItems.sumOf { it.product.price * it.quantity }

                submitOrderToBackend(
                    context = context,
                    cartItems = cartItems,
                    name = name,
                    phone = phone,
                    address = address,
                    email = email,
                    baseUrl = "http://192.168.244.92:8080",
                    paymentMethod = "UPI"
                )

                launchUPIPayment(
                    context = context,
                    name = name,
                    upiId = "guptakshitij266@oksbi",
                    amount = totalAmount.toString()
                )
            }
        )
    }
}

@Composable
fun EmptyCartUI(navController: NavHostController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🛒 Your cart is empty!", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { navController.navigate("home") }) {
            Text("Go to Home")
        }
    }
}

@Composable
fun CheckoutBar(cartItems: List<CartItem>, onCheckoutClick: () -> Unit) {
    val total = cartItems.sumOf { it.product.price * it.quantity }
    Surface(shadowElevation = 6.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total: ₹$total", fontWeight = FontWeight.Bold)
            Button(onClick = onCheckoutClick) {
                Text("Checkout")
            }
        }
    }
}

@Composable
fun CheckoutDialog(
    cartItems: List<CartItem>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val isFormValid = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { onConfirm(name, phone, address, email) }, enabled = isFormValid) {
                Text("Proceed to Payment")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Enter Shipping Details") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email (optional)") }, modifier = Modifier.fillMaxWidth())
            }
        }
    )
}

fun submitOrderToBackend(
    context: Context,
    cartItems: List<CartItem>,
    name: String,
    phone: String,
    address: String,
    email: String,
    baseUrl: String,
    paymentMethod: String
) {
    val orderId = UUID.randomUUID().toString()
    val totalAmount = cartItems.sumOf { it.product.price * it.quantity }

    val itemsArray = JSONArray().apply {
        cartItems.forEach {
            put(JSONObject().apply {
                put("name", it.product.name)
                put("price", it.product.price)
                put("quantity", it.quantity)
            })
        }
    }

    val orderJson = JSONObject().apply {
        put("orderId", orderId)
        put("name", name)
        put("phone", phone)
        put("address", address)
        put("email", email)
        put("totalAmount", totalAmount)
        put("items", itemsArray)
        put("paymentMethod", paymentMethod)
    }

    val requestBody = orderJson.toString().toRequestBody("application/json".toMediaTypeOrNull())
    val request = Request.Builder()
        .url("$baseUrl/api/orders")
        .post(requestBody)
        .build()

    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            showToast(context, "❌ Failed to place order")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                CoroutineScope(Dispatchers.Main).launch {
                    showToast(context, "✅ Order placed successfully")
                }
            } else {
                showToast(context, "❌ Error: ${response.message}")
            }
        }
    })
}

fun launchUPIPayment(context: Context, name: String, upiId: String, amount: String, note: String = "IoT Bazaar Payment") {
    val uri = Uri.Builder()
        .scheme("upi")
        .authority("pay")
        .appendQueryParameter("pa", upiId)
        .appendQueryParameter("pn", name)
        .appendQueryParameter("tn", note)
        .appendQueryParameter("am", amount)
        .appendQueryParameter("cu", "INR")
        .build()

    val intent = Intent(Intent.ACTION_VIEW, uri)
    val chooser = Intent.createChooser(intent, "Pay with")

    try {
        context.startActivity(chooser)
    } catch (e: Exception) {
        showToast(context, "No UPI app found!")
    }
}

fun showToast(context: Context, message: String) {
    CoroutineScope(Dispatchers.Main).launch {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun CartItemRow(cartItem: CartItem, cartViewModel: CartViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
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
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(cartItem.product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("₹${cartItem.product.price} x ${cartItem.quantity}", color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = { cartViewModel.decreaseQuantity(cartItem.product) },
                        enabled = cartItem.quantity > 1
                    ) {
                        Text("-")
                    }
                    Text(
                        text = cartItem.quantity.toString(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    OutlinedButton(onClick = { cartViewModel.addToCart(cartItem.product) }) {
                        Text("+")
                    }
                }
            }
            IconButton(onClick = { cartViewModel.removeFromCart(cartItem.product) }) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
