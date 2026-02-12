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
    val currency: Currency,
    val tags: List<TagRequestResponse> = emptyList(),
) {
    fun toInvoice(
        id: UUID,
        invoiceNumber: Int,
        household: Household,
        products: Map<UUID, Product>,
    ): Invoice {
        val mappedInvoiceLines = invoiceLines.map { it.toInvoiceLine(products) }
        val totals = calculateTotals(mappedInvoiceLines)
        return Invoice(
            id = id,
            invoiceNumber = invoiceNumber,
            status = status,
            generatedDate = generatedDate,
            dueDate = dueDate,
            finalizedDate = finalizedDate,
            household = household,
            invoiceLines = mappedInvoiceLines,
            price = totals.price,
            tax = totals.tax,
            totalPrice = totals.total,
            currency = currency,
            tags = tags.map { it.toTag() },
            invoiceFilePath = null,
        )
    }

    private fun calculateTotals(lines: Collection<InvoiceLine>): Totals {
        var price = 0.0
        var tax = 0.0
        lines.forEach { item ->
            price += item.price
            tax += item.tax
        }
        return Totals(price = price, tax = tax, total = price + tax)
    }
}

@Serializable
data class InvoiceLineRequest(
    @Serializable(with = UUIDSerializer::class) val id: UUID? = null,
    @SerialName("line_number") val lineNumber: Int,
    @Serializable(with = UUIDSerializer::class) @SerialName("product_id") val productId: UUID,
    val quantity: Int,
    val currency: Currency,
) {
    fun toInvoiceLine(products: Map<UUID, Product>): InvoiceLine {
        val product = products[productId] ?: throw RuntimeException("Product not found")
        val totals = calculateTotals(product)
        return InvoiceLine(
            // Create an ID if this is a new invoice line
            id = id ?: UUID.randomUUID(),
            lineNumber = lineNumber,
            product = product,
            quantity = quantity,
            price = totals.price,
            tax = totals.tax,
            totalPrice = totals.total,
            currency = currency,
        )
    }

    private fun calculateTotals(product: Product): Totals {
        val price = product.price * quantity
        val tax = ((product.taxPercentage * product.price) / 100) * quantity
        return Totals(price = price, tax = tax, total = price + tax)
    }
}

private data class Totals(
    val price: Double,
    val tax: Double,
    val total: Double,
)
