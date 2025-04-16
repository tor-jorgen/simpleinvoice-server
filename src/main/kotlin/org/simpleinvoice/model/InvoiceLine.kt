package org.simpleinvoice.model

import kotlinx.serialization.Serializable
import org.simpleinvoice.common.UUIDSerializer
import java.util.UUID

@Serializable
class InvoiceLine(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val lineNumber: Int,
    val product: Product,
    val quantity: Int,
    val totalPrice: Double,
    val currency: Currency,
)
