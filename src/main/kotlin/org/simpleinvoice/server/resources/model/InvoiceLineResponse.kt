package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.InvoiceLine
import java.util.UUID

@Serializable
data class InvoiceLineResponse(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @SerialName("line_number") val lineNumber: Int,
    val product: ProductResponse,
    val quantity: Int,
    val price: String,
    val tax: String,
    @SerialName("total_price") val totalPrice: String,
    val currency: Currency,
) {
    companion object {
        fun fromInvoiceLine(line: InvoiceLine) =
            InvoiceLineResponse(
                id = line.id,
                lineNumber = line.lineNumber,
                product = ProductResponse.fromProduct(line.product),
                quantity = line.quantity,
                price = line.price.toString(),
                tax = line.tax.toString(),
                totalPrice = line.totalPrice.toString(),
                currency = line.currency,
            )
    }
}
