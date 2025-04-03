package com.example.iotbazaar.ui.screens.home

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.iotbazaar.viewmodel.CartViewModel
import com.example.iotbazaar.viewmodel.Product
import com.example.iotbazar.BottomNavBar
import com.google.accompanist.swiperefresh.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// ✅ API Base URL
private const val BASE_URL = "http://192.168.243.92:8080/api/"

// ✅ Retrofit API Interface
interface ProductService {
    @GET("products")
    suspend fun getProducts(): List<Product>
}

// ✅ Home Screen
@Composable
fun HomeScreen(navController: NavController, cartViewModel: CartViewModel = viewModel()) {
    val products = remember { mutableStateListOf<Product>() }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val refreshState = rememberSwipeRefreshState(isLoading)

    // ✅ Fetch Products
    fun fetchProducts() {
        scope.launch {
            isLoading = true
            try {
                val api = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ProductService::class.java)

                products.clear()
                products.addAll(api.getProducts())
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
            Column {
                HomeTopAppBar(
                    navController, cartViewModel, searchQuery, isSearchActive,
                    onSearchQueryChanged = { searchQuery = it },
                    onSearchToggle = { isSearchActive = it }
                )

                // ✅ Animated Search Bar
                AnimatedVisibility(
                    visible = isSearchActive,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                ) {
                    SearchBar(searchQuery) { searchQuery = it }
                }
            }
        },
        bottomBar = { BottomNavBar(navController, "home") }
    ) { padding ->
        SwipeRefresh(
            state = refreshState,
            onRefresh = { fetchProducts() }
        ) {
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
                        val filteredProducts = products.filter { it.name.contains(searchQuery, ignoreCase = true) }
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

// ✅ Search Bar Component
@Composable
fun SearchBar(searchQuery: String, onSearchQueryChanged: (String) -> Unit) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        placeholder = { Text("Search products...") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

// ✅ Top App Bar with Animated Search
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
        title = { Text("IoT Bazaar", color = MaterialTheme.colorScheme.onPrimary) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
        actions = {
            IconButton(onClick = { onSearchToggle(!isSearchActive) }) {
                Icon(
                    imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            IconButton(onClick = { navController.navigate("cart") }) {
                BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    )
}

// ✅ Product Grid Layout
@Composable
fun ProductGrid(products: List<Product>, cartViewModel: CartViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        items(products) { product ->
            ProductCard(product, cartViewModel)
        }
    }
}

// ✅ Product Card UI
@Composable
fun ProductCard(product: Product, cartViewModel: CartViewModel) {
    val fullImageUrl = if (product.imageUrl.startsWith("http")) product.imageUrl else "$BASE_URL${product.imageUrl}"
    val cartItems by cartViewModel.cartItems.collectAsState()
    val isInCart = cartItems.any { it.product.id == product.id }

    // Use a state variable to track the button state for this specific product
    var isAdded by remember { mutableStateOf(isInCart) }

    Card(
        Modifier.padding(8.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(fullImageUrl)
                    .crossfade(true).build(),
                contentDescription = product.name,
                modifier = Modifier.size(130.dp).padding(4.dp).clickable {}
            )
            Spacer(Modifier.height(8.dp))
            Text(
                product.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "₹${product.price}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (!isAdded) {
                        cartViewModel.addToCart(product)
                        isAdded =
                            true // Update the local state to reflect that the product has been added
                    }
                },
                enabled = !isAdded,
                colors = ButtonDefaults.buttonColors(containerColor = if (isAdded) Color.Gray else MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (isAdded) "Added" else "Add to Cart",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }}}


@Composable
fun NoProductsFound() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🚫 No products found.", color = MaterialTheme.colorScheme.error)
    }
}

// ✅ UI for Retry on API Error
@Composable
fun RetrySection(onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("❌ Failed to load products. Please try again.", color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry) { Text("Retry") }
    }
}

// ✅ UI for Empty Product List
@Composable
fun EmptyState() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("📭 No products available.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}