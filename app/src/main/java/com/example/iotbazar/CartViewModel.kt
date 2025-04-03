package com.example.iotbazaar.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class CartItem(val product: Product, var quantity: Int)

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _addedProducts = MutableStateFlow<Set<String>>(emptySet()) // Track added product IDs
    val addedProducts: StateFlow<Set<String>> = _addedProducts

    fun addToCart(product: Product) {
        _cartItems.update { currentList ->
            val existingItem = currentList.find { it.product.id == product.id }
            if (existingItem != null) {
                currentList.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                currentList + CartItem(product, quantity = 1)
            }
        }

        // Mark product as added
        _addedProducts.update { it + product.id }
    }

    fun decreaseQuantity(product: Product) {
        _cartItems.update { currentList ->
            currentList.mapNotNull {
                if (it.product.id == product.id) {
                    if (it.quantity > 1) it.copy(quantity = it.quantity - 1) else null
                } else it
            }
        }
    }

    fun removeFromCart(product: Product) {
        _cartItems.update { it.filter { cartItem -> cartItem.product.id != product.id } }
        _addedProducts.update { it - product.id } // Remove from added products set
    }

    fun clearCart() {  // ✅ Moved inside the class
        _cartItems.value = emptyList()
        _addedProducts.value = emptySet() // ✅ Reset addedProducts when clearing the cart
    }
}
