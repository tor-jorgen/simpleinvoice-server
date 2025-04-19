package org.simpleinvoice.server.model

import kotlinx.serialization.Serializable

@Serializable
enum class InvoiceStatus {
    UNPAID,
    PAID,
    REPLACED,
    CANCELLED,
}
