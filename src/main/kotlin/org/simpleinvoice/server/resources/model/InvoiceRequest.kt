package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.InstantSerializer
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceLine
import org.simpleinvoice.server.model.InvoiceStatus
import org.simpleinvoice.server.model.Product
import java.time.Instant
import java.util.UUID

@Serializable
data class InvoiceRequest(
    val status: InvoiceStatus,
    @SerialName("generated_date") @Serializable(with = InstantSerializer::class) val generatedDate: Instant,
    @SerialName("due_date") @Serializable(with = InstantSerializer::class) val dueDate: Instant,
    @SerialName("finalized_date") @Serializable(with = InstantSerializer::class) val finalizedDate: Instant? = null,
    val household: Household,
    @SerialName("invoice_lines") val invoiceLines: List<InvoiceLineRequest>,
    @SerialName("total_price") val totalPrice: Double,
    val currency: Currency,
    val tags: List<TagRequest>,
) {
    fun toInvoice(
        id: UUID,
        invoiceNumber: Int,
    ): Invoice =
        Invoice(
            id = id,
            invoiceNumber = invoiceNumber,
            status = status,
            generatedDate = generatedDate,
            dueDate = dueDate,
            finalizedDate = finalizedDate,
            household = household,
            invoiceLines = invoiceLines.map { it.toInvoiceLine() },
            totalPrice = totalPrice,
            currency = currency,
            tags = tags.map { it.toTag(it.id!!) },
            invoiceFilePath = null,
        )
}

@Serializable
data class InvoiceLineRequest(
    @Serializable(with = UUIDSerializer::class) val id: UUID? = null,
    @SerialName("line_number") val lineNumber: Int,
    val product: Product,
    val quantity: Int,
    @SerialName("total_price") val totalPrice: Double,
    val currency: Currency,
) {
    fun toInvoiceLine(): InvoiceLine =
        InvoiceLine(
            // Create an ID if this is a new invoice line
            id = id ?: UUID.randomUUID(),
            lineNumber = lineNumber,
            product = product,
            quantity = quantity,
            totalPrice = totalPrice,
            currency = currency,
        )
}
