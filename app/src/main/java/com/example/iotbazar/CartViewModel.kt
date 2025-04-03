package com.example.iotbazaar.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class CartItem(val product: Product, var quantity: Int)

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    fun addToCart(product: Product) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find { it.product.id == product.id }

        if (existingItem == null) {
            currentItems.add(CartItem(product, 1)) // Add with quantity 1
        } else {
            existingItem.quantity += 1 // Increase quantity if already in cart
        }

        _cartItems.value = currentItems // ✅ Update StateFlow
    }

    fun removeFromCart(product: Product) {
        val currentItems = _cartItems.value.toMutableList()
        currentItems.removeAll { it.product.id == product.id }
        _cartItems.value = currentItems
    }

    fun decreaseQuantity(product: Product) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find { it.product.id == product.id }

        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                existingItem.quantity -= 1 // Decrease quantity
            } else {
                removeFromCart(product) // Remove item if quantity is 1
            }
        }

        _cartItems.value = currentItems // ✅ Update StateFlow
    }
}