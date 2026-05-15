package org.simpleinvoice.server.invoice

data class InvoiceConfig(
//    val documentsDirectory: String,
    val invoiceBucketName: String,
    val configBucketName: String,
    val invoiceTemplateName: String,
    val invoiceName: String,
)
