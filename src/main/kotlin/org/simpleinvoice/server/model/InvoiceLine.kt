package org.simpleinvoice.server.model

import java.util.UUID

data class InvoiceLine(
    val id: UUID,
    val lineNumber: Int,
    val product: Product,
    val quantity: Int,
    val price: Double,
    val tax: Double,
    val totalPrice: Double,
    val currency: Currency,
)
