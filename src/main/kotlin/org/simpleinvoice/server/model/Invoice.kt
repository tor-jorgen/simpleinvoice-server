package org.simpleinvoice.server.model

import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.InstantSerializer
import org.simpleinvoice.server.common.UUIDSerializer
import java.time.Instant
import java.util.UUID

@Serializable
data class Invoice(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val invoiceNumber: Int,
    val status: InvoiceStatus,
    @Serializable(with = InstantSerializer::class) val generatedDate: Instant,
    @Serializable(with = InstantSerializer::class) val dueDate: Instant,
    @Serializable(with = InstantSerializer::class) val finalizedDate: Instant?,
    val household: Household,
    val invoiceLines: List<InvoiceLine>,
    val totalPrice: Double,
    val currency: Currency,
)
