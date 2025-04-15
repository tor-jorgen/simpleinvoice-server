package org.simpleinvoice.model

import com.example.org.simpleinvoice.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class InvoiceLine(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val lineNumber: Int,
    val product: Product,
    val quantity: Int,
    val totalPrice: Double,
)
