package org.simpleinvoice.server.invoice

data class InvoiceConfig(
    val invoiceDirectory: String,
    val template: String,
    val invoiceName: String,
)
