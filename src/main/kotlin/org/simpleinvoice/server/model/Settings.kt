package org.simpleinvoice.server.model

import java.util.UUID

data class Settings(
    val id: UUID,
    val defaultDueDays: Int,
    val lastInvoiceNumber: Int,
    val defaultTaxPercentage: Double,
    val defaultCurrency: Currency,
    val defaultEmailSubject: String?,
    val defaultEmailText: String?,
)
