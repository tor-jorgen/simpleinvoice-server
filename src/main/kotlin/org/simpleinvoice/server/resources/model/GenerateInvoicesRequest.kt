package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.InstantSerializer
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Email
import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceLine
import org.simpleinvoice.server.model.InvoiceStatus
import org.simpleinvoice.server.model.Product
import java.time.Instant
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class GenerateInvoicesRequest
    @OptIn(ExperimentalUuidApi::class)
    constructor(
        @Serializable(with = InstantSerializer::class) @SerialName("due_date") val dueDate: Instant,
        val price: Double = 0.0,
        val tax: Double = 0.0,
        @SerialName("total_price") val totalPrice: Double,
        val currency: Currency,
        @SerialName("invoice_lines") val invoiceLines: List<GenerateInvoiceLineRequest>,
        // Uses kotlin.uuid.Uuid to be able to serialize a list of UUIDs
        @SerialName("household_ids") val householdIds: List<Uuid>,
        val tags: List<TagRequest>,
        val email: EmailRequest? = null,
        val message: String? = null,
    ) {
        // Create an invoice with only necessary properties set. The others will be set/calculated later
        fun toInvoice() =
            Invoice(
                id = UUID.randomUUID(),
                invoiceNumber = 0,
                status = InvoiceStatus.CREATED,
                generatedDate = Instant.now(),
                dueDate = dueDate,
                finalizedDate = null,
                price = price,
                tax = tax,
                totalPrice = totalPrice,
                currency = currency,
                household =
                    Household(
                        // Dummy household
                        id = UUID.randomUUID(),
                        address = "",
                        zipCode = "",
                        city = "",
                        persons = emptyList(),
                    ),
                invoiceFilePath = null,
                invoiceLines = invoiceLines.map { it.toInvoiceLine() },
                tags = tags.map { it.toTag() },
            )
    }

@Serializable
data class GenerateInvoiceLineRequest
    @OptIn(ExperimentalUuidApi::class)
    constructor(
        @SerialName("line_number") val lineNumber: Int,
        @Serializable(with = UUIDSerializer::class) @SerialName("product_id") val productId: UUID,
        val quantity: Int,
        @SerialName("total_price") val totalPrice: Double,
        val currency: Currency,
        val price: Double,
        val tax: Double,
    ) {
        // Create an invoiceLine with only necessary properties set. The others will be set/calculated later
        fun toInvoiceLine() =
            InvoiceLine(
                id = UUID.randomUUID(),
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
                price = price,
                tax = tax,
                totalPrice = totalPrice,
                currency = currency,
            )
    }

@Serializable
data class EmailRequest(
    val subject: String,
    val text: String? = null,
) {
    fun toEmail() = Email(subject = subject, text = text)
}
