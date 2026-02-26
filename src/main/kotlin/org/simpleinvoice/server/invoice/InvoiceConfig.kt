package org.simpleinvoice.server.invoice

data class InvoiceConfig(
    val invoiceDirectory: String,
    val configDirectory: String,
    val invoiceTemplateName: String,
    val invoiceName: String,
)
