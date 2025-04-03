package com.example.iotbazaar.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class CartItem(val product: Product, var quantity: Int)

class CartViewModel : ViewModel() {
    // Use MutableStateFlow to manage cart items and make it observable in Jetpack Compose
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    fun addToCart(product: Product) {
        val currentList = _cartItems.value.toMutableList()

        val existingItemIndex = currentList.indexOfFirst { it.product.id == product.id }
        if (existingItemIndex != -1) {
            // Increase quantity if the product is already in the cart
            currentList[existingItemIndex] = currentList[existingItemIndex].copy(quantity = currentList[existingItemIndex].quantity + 1)
        } else {
            // Add new product instance
            currentList.add(CartItem(product, quantity = 1))
        }

        _cartItems.value = currentList
    }

    // Decrease quantity of a product in the cart
    fun decreaseQuantity(product: Product) {
        val updatedCart = _cartItems.value.mapNotNull {
            if (it.product.id == product.id) {
                if (it.quantity > 1) it.copy(quantity = it.quantity - 1) else null
            } else it
        }
        _cartItems.value = updatedCart
    }

    // Remove product from the cart
    fun removeFromCart(product: Product) {
        _cartItems.value = _cartItems.value.filter { it.product.id != product.id }
    }
}
