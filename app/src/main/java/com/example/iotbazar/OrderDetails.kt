package com.example.iotbazar

data class OrderDetails(
    val name: String,
    val phone: String,
    val email: String,
    val address: String,
    val totalAmount: Double,
    val paymentMethod: String
)
