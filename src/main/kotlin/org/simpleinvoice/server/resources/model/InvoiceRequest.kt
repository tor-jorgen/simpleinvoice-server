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
    @Serializable(with = UUIDSerializer::class) @SerialName("household_id") val householdId: UUID,
    @SerialName("invoice_lines") val invoiceLines: List<InvoiceLineRequest>,
    @SerialName("total_price") val totalPrice: Double,
    val currency: Currency,
    val tags: List<TagRequest> = emptyList(),
) {
    fun toInvoice(
        id: UUID,
        invoiceNumber: Int,
        household: Household,
        products: Map<UUID, Product>,
    ): Invoice =
        Invoice(
            id = id,
            invoiceNumber = invoiceNumber,
            status = status,
            generatedDate = generatedDate,
            dueDate = dueDate,
            finalizedDate = finalizedDate,
            household = household,
            invoiceLines = invoiceLines.map { it.toInvoiceLine(products) },
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
    @Serializable(with = UUIDSerializer::class) @SerialName("product_id") val productId: UUID,
    val quantity: Int,
    @SerialName("total_price") val totalPrice: Double,
    val currency: Currency,
) {
    fun toInvoiceLine(products: Map<UUID, Product>): InvoiceLine =
        InvoiceLine(
            // Create an ID if this is a new invoice line
            id = id ?: UUID.randomUUID(),
            lineNumber = lineNumber,
            product = products[productId] ?: throw RuntimeException("Product not found"),
            quantity = quantity,
            totalPrice = totalPrice,
            currency = currency,
        )
}
