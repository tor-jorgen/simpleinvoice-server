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
    val item: InvoiceRequestDTO,
    val message: String? = null,
) {
    fun toInvoice(
        id: UUID,
        invoiceNumber: Int,
    ) = item.toInvoice(id, invoiceNumber)
}

@Serializable
data class InvoiceRequestDTO(
    val status: InvoiceStatus,
    @SerialName("generated_date") @Serializable(with = InstantSerializer::class) val generatedDate: Instant,
    @SerialName("due_date") @Serializable(with = InstantSerializer::class) val dueDate: Instant,
    @SerialName("finalized_date") @Serializable(with = InstantSerializer::class) val finalizedDate: Instant? = null,
    @Serializable(with = UUIDSerializer::class) @SerialName("household_id") val householdId: UUID,
    @SerialName("invoice_lines") val invoiceLines: List<InvoiceLineRequestObject>,
    val currency: Currency,
    val tags: List<TagDTO> = emptyList(),
) {
    // Create an invoice with only necessary properties set. The others will be set/calculated later
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
            household =
                Household(
                    id = householdId,
                    address = "",
                    zipCode = "",
                    city = "",
                    persons = emptyList(),
                ),
            invoiceLines = invoiceLines.map { it.toInvoiceLine() },
            price = 0.0,
            tax = 0.0,
            totalPrice = 0.0,
            currency = currency,
            tags = tags.map { it.toTag() },
            invoiceFilePath = null,
        )
}

@Serializable
data class InvoiceLineRequestObject(
    @Serializable(with = UUIDSerializer::class) val id: UUID? = null,
    @SerialName("line_number") val lineNumber: Int,
    @Serializable(with = UUIDSerializer::class) @SerialName("product_id") val productId: UUID,
    val quantity: Int,
    val currency: Currency,
) {
    // Create an invoiceLine with only necessary properties set. The others will be set/calculated later
    fun toInvoiceLine(): InvoiceLine =
        InvoiceLine(
            // Create an ID if this is a new invoice line
            id = id ?: UUID.randomUUID(),
            lineNumber = lineNumber,
            product =
                Product(
                    id = productId,
                    quantity = 0,
                    code = "",
                    name = "",
                    price = 0.0,
                    currency = Currency.NONE,
                    taxPercentage = 0.0,
                    tax = 0.0,
                    totalPrice = 0.0,
                ),
            quantity = quantity,
            price = 0.0,
            tax = 0.0,
            totalPrice = 0.0,
            currency = currency,
        )
}
