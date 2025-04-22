package org.simpleinvoice.server.resources.model

import kotlinx.serialization.Serializable
import org.simpleinvoice.server.model.Invoice

@Serializable
data class InvoicesResponse(
    val invoices: List<Invoice>,
)
