package com.example.iotbazaar.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.iotbazaar.viewmodel.CartViewModel
import com.example.iotbazar.BottomNavBar
import com.google.accompanist.swiperefresh.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import com.example.iotbazaar.viewmodel.Product

private const val BASE_URL = "http://192.168.109.92:8080/api/"

interface ProductService {
    @GET("products")
    suspend fun getProducts(): List<Product>
}

@Composable
fun HomeScreen(navController: NavController, cartViewModel: CartViewModel) {
    val products = remember { mutableStateListOf<Product>() }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val refreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    fun fetchProducts() {
        scope.launch {
            isLoading = true
            try {
                val api = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ProductService::class.java)

                val fetchedProducts = api.getProducts()
                products.clear()
                products.addAll(fetchedProducts)
                isError = false
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to fetch products: ${e.message}")
                isError = true
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { fetchProducts() }

    Scaffold(
        topBar = {
            HomeTopAppBar(
                navController,
                cartViewModel,
                searchQuery,
                isSearchActive,
                onSearchQueryChanged = { searchQuery = it },
                onSearchToggle = { isSearchActive = it }
            )
        },
        bottomBar = { BottomNavBar(navController, "home") }
    ) { padding ->
        SwipeRefresh(state = refreshState, onRefresh = { fetchProducts() }) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    isError -> RetrySection { fetchProducts() }
                    products.isEmpty() -> EmptyState()
                    else -> {
                        val filteredProducts = products.filter {
                            it.name.contains(searchQuery, ignoreCase = true)
                        }
                        if (filteredProducts.isEmpty()) {
                            NoProductsFound()
                        } else {
                            ProductGrid(filteredProducts, cartViewModel)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    navController: NavController,
    cartViewModel: CartViewModel,
    searchQuery: String,
    isSearchActive: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onSearchToggle: (Boolean) -> Unit
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartCount = cartItems.sumOf { it.quantity }

    TopAppBar(
        title = {
            if (isSearchActive) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    placeholder = { Text("Search products…", color = Color.White.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLeadingIconColor = Color.White,
                        unfocusedLeadingIconColor = Color.White,
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.4f)
                    )
                )
            } else {
                Text("IoT Bazaar", color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        actions = {
            IconButton(onClick = { onSearchToggle(!isSearchActive) }) {
                Icon(
                    imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
            IconButton(onClick = { navController.navigate("cart") }) {
                BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = Color.White
                    )
                }
            }
        }
    )
}

@Composable
fun ProductGrid(products: List<Product>, cartViewModel: CartViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCard(product, cartViewModel)
        }
    }
}

@Composable
fun ProductCard(product: Product, cartViewModel: CartViewModel) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val context = LocalContext.current
    var showImageDialog by remember { mutableStateOf(false) }
    val isAdded = cartItems.any { it.product.id == product.id }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(
                        if (product.imageUrl.startsWith("http")) product.imageUrl
                        else "$BASE_URL${product.imageUrl.trimStart('/')}"
                    )
                    .crossfade(true)
                    .build(),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showImageDialog = true }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(product.name, style = MaterialTheme.typography.titleMedium)
            Text("₹${product.price}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { cartViewModel.addToCart(product) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = !isAdded
            ) {
                Text(if (isAdded) "Added" else "Add to Cart", color = Color.White)
            }
        }
    }

    if (showImageDialog) {
        Dialog(onDismissRequest = { showImageDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .clickable { showImageDialog = false },
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = product.imageUrl,
                    contentDescription = "Full image of ${product.name}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}

@Composable
fun RetrySection(onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Failed to load products.", color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun NoProductsFound() {
    Text("🚫 No products found.", color = MaterialTheme.colorScheme.error)
}

@Composable
fun EmptyState() {
    Text("📭 No products available.", color = MaterialTheme.colorScheme.onSurfaceVariant)
}
