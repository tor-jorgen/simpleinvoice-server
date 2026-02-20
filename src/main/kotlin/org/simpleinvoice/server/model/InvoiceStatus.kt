package org.simpleinvoice.server.model

import kotlinx.serialization.Serializable

@Serializable
enum class InvoiceStatus {
    CREATED,
    DELIVERED,
    PAID,
    REPLACED,
    CANCELLED,
}
