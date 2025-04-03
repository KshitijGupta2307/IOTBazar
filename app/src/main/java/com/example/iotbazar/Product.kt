package com.example.iotbazaar.viewmodel

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("_id") val id: String,  // ✅ Maps MongoDB "_id" to Kotlin "id"
    val name: String,
    @SerializedName("imageUrl") val imageUrl: String,
    val price: Double
)
