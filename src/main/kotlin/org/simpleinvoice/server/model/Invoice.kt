package org.simpleinvoice.server.model

import java.time.Instant
import java.util.UUID

data class Invoice(
    val id: UUID,
    val invoiceNumber: Int,
    val status: InvoiceStatus,
    val generatedDate: Instant,
    val dueDate: Instant,
    val finalizedDate: Instant?,
    val price: Double,
    val tax: Double,
    val totalPrice: Double,
    val currency: Currency,
    val household: Household,
    val invoiceFilePath: String?,
    val invoiceLines: List<InvoiceLine>,
    val tags: List<Tag>,
) {
    fun generatedDateAsString(): String = generatedDate.toString().substring(0, 10)

    fun dueDateAsString(): String = dueDate.toString().substring(0, 10)

    fun finalizedDateAsString(): String = finalizedDate?.toString()?.substring(0, 10) ?: ""
}
