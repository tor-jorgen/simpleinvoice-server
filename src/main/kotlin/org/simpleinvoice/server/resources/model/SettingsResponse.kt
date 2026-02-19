package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Settings
import java.util.UUID

@Serializable
data class SettingsResponse(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @SerialName("default_due_days") val defaultDueDays: Int,
    @SerialName("last_invoice_number") val lastInvoiceNumber: Int,
    @SerialName("default_tax_percentage") val defaultTaxPercentage: Double,
    @SerialName("default_currency") val defaultCurrency: Currency,
    @SerialName("default_email_subject") val defaultEmailSubject: String?,
    @SerialName("default_email_text") val defaultEmailText: String?,
) {
    companion object {
        fun fromSettings(settings: Settings) =
            SettingsResponse(
                id = settings.id,
                defaultDueDays = settings.defaultDueDays,
                lastInvoiceNumber = settings.lastInvoiceNumber,
                defaultTaxPercentage = settings.defaultTaxPercentage,
                defaultCurrency = settings.defaultCurrency,
                defaultEmailSubject = settings.defaultEmailSubject,
                defaultEmailText = settings.defaultEmailText,
            )
    }
}
