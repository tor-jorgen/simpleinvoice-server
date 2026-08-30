package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Settings
import java.util.UUID

@Serializable
class SettingsRequest(
    val settings: SettingsRequestDTO,
    val message: String? = null,
) {
    fun toSettings(id: UUID): Settings = settings.toSettings(id)
}

@Serializable
class SettingsRequestDTO(
    @SerialName("default_due_days") val defaultDueDays: Int,
    @SerialName("last_invoice_number") val lastInvoiceNumber: Int,
    @SerialName("default_tax_percentage") val defaultTaxPercentage: Double,
    @SerialName("default_currency") val defaultCurrency: Currency,
    @SerialName("default_email_subject") val defaultEmailSubject: String?,
    @SerialName("default_email_text") val defaultEmailText: String?,
) {
    fun toSettings(id: UUID): Settings =
        Settings(
            id = id,
            defaultDueDays = defaultDueDays,
            lastInvoiceNumber = lastInvoiceNumber,
            defaultTaxPercentage = defaultTaxPercentage,
            defaultCurrency = defaultCurrency,
            defaultEmailSubject = defaultEmailSubject,
            defaultEmailText = defaultEmailText,
        )
}
