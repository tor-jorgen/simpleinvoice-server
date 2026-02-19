package org.simpleinvoice.server.model

import java.util.UUID

data class Product(
    val id: UUID,
    val code: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val currency: Currency,
    val taxPercentage: Double,
    val tax: Double,
    val totalPrice: Double,
    val tags: List<Tag> = emptyList(),
    val inactive: Boolean = false,
)
