package org.simpleinvoice.server.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import java.util.UUID

@Serializable
data class Settings(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @SerialName("default_due_days") val defaultDueDays: Int,
    @SerialName("last_invoice_number") val lastInvoiceNumber: Int,
    @SerialName("default_tax_percentage") val defaultTaxPercentage: Double,
    @SerialName("default_currency") val defaultCurrency: Currency,
    @SerialName("default_email_subject") val defaultEmailSubject: String?,
    @SerialName("default_email_text") val defaultEmailText: String?,
)
