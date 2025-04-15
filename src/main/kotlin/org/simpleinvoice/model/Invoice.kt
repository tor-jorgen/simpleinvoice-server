package org.simpleinvoice.model

import com.example.org.simpleinvoice.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class Invoice(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val number: Int,
    val generated: String,
    val dueDate: String,
    val customer: Customer,
    val invoicelines: List<InvoiceLine>,
)
