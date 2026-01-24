package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.InstantSerializer
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceStatus
import java.time.Instant
import java.util.UUID

@Serializable
data class InvoiceResponse(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @SerialName("invoice_number") val invoiceNumber: Int,
    val status: InvoiceStatus,
    @SerialName("generated_date") @Serializable(with = InstantSerializer::class) val generatedDate: Instant,
    @SerialName("due_date") @Serializable(with = InstantSerializer::class) val dueDate: Instant,
    @SerialName("finalized_date") @Serializable(with = InstantSerializer::class) val finalizedDate: Instant?,
    val price: String,
    val tax: String,
    @SerialName("total_price") val totalPrice: String,
    val currency: Currency,
    val household: HouseholdResponse,
    @SerialName("invoice_file_path") val invoiceFilePath: String?,
    @SerialName("invoice_lines") val invoiceLines: List<InvoiceLineResponse>,
    val tags: List<TagRequestResponse>,
) {
    companion object {
        fun fromInvoice(invoice: Invoice) =
            InvoiceResponse(
                id = invoice.id,
                invoiceNumber = invoice.invoiceNumber,
                status = invoice.status,
                generatedDate = invoice.generatedDate,
                dueDate = invoice.dueDate,
                finalizedDate = invoice.finalizedDate,
                price = invoice.price.toString(),
                tax = invoice.tax.toString(),
                totalPrice = invoice.totalPrice.toString(),
                currency = invoice.currency,
                household = HouseholdResponse.fromHousehold(invoice.household),
                invoiceFilePath = invoice.invoiceFilePath,
                invoiceLines = invoice.invoiceLines.map { InvoiceLineResponse.fromInvoiceLine(it) },
                tags = invoice.tags.map { TagRequestResponse.fromTag(it) },
            )
    }
}
