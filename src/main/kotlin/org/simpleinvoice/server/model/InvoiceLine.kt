package org.simpleinvoice.server.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import java.util.UUID

@Serializable
class InvoiceLine(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @SerialName("line_number") val lineNumber: Int,
    val product: Product,
    val quantity: Int,
    @SerialName("total_price") val totalPrice: Double,
    val currency: Currency,
) {
    fun toInvoiceLine(): InvoiceLine =
        InvoiceLine(
            id = id,
            lineNumber = lineNumber,
            product = product,
            quantity = quantity,
            totalPrice = totalPrice,
            currency = currency,
        )
}
