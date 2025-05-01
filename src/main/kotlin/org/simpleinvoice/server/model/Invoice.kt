package org.simpleinvoice.server.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.InstantSerializer
import org.simpleinvoice.server.common.UUIDSerializer
import java.time.Instant
import java.util.UUID

@Serializable
data class Invoice(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @SerialName("invoice_number") val invoiceNumber: Int,
    val status: InvoiceStatus,
    @SerialName("generated_date") @Serializable(with = InstantSerializer::class) val generatedDate: Instant,
    @SerialName("due_date") @Serializable(with = InstantSerializer::class) val dueDate: Instant,
    @SerialName("finalized_date") @Serializable(with = InstantSerializer::class) val finalizedDate: Instant?,
    @SerialName("total_price") val totalPrice: Double,
    val currency: Currency,
    val household: Household,
    @SerialName("invoice_lines") val invoiceLines: List<InvoiceLine>,
)
