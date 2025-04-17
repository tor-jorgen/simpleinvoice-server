package org.simpleinvoice.server.model

import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.InstantSerializer
import org.simpleinvoice.server.common.UUIDSerializer
import java.time.Instant
import java.util.UUID

@Serializable
class InvoiceReminder(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val number: Int,
    @Serializable(with = InstantSerializer::class) val generatedDate: Instant,
    @Serializable(with = InstantSerializer::class) val dueDate: Instant,
    @Serializable(with = InstantSerializer::class) val paidDate: Instant,
    val household: Household,
    val invoicelines: List<InvoiceLine>,
    val totalPrice: Double,
    val currency: Currency,
)
