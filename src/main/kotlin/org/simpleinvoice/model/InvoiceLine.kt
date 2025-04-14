package com.example.org.simpleinvoice.model

import kotlinx.serialization.Serializable

@Serializable
class InvoiceLine(
    val id: String,
    val product: Product,
    val quantity: Int,
)
