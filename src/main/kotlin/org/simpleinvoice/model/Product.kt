package com.example.org.simpleinvoice.model

import kotlinx.serialization.Serializable

@Serializable
class Product(
    val code: String,
    val name: String,
    val price: Double,
    val currency: String,
)
