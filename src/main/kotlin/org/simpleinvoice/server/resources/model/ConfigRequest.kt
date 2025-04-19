package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.model.Config
import org.simpleinvoice.server.model.Currency
import java.util.UUID

@Serializable
class ConfigRequest(
    @SerialName("default_due_days") val defaultDueDays: Int,
    @SerialName("last_invoice_number") val lastInvoiceNumber: Int,
    @SerialName("default_currency") val defaultCurrency: Currency,
) {
    fun toConfig(id: UUID): Config =
        Config(
            id = id,
            defaultDueDays = defaultDueDays,
            lastInvoiceNumber = lastInvoiceNumber,
            defaultCurrency = defaultCurrency,
        )
}
