package org.simpleinvoice.model

import com.example.org.simpleinvoice.common.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class Config(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @SerialName("default_due_days") val defaultDueDays: Int,
    @SerialName("last_invoice_number") val lastInvoiceNumber: Int,
    @SerialName("default_currency") val defaultCurrency: String,
)
