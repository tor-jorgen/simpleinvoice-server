package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Product
import java.util.UUID

@Serializable
class ProductRequest(
    val code: String,
    val name: String,
    val quantity: Int,
    val price: String,
    val currency: Currency,
    @SerialName("tax_percentage") val taxPercentage: String,
    val tags: List<TagRequestResponse> = emptyList(),
    val inactive: Boolean,
) {
    fun toProduct(id: UUID): Product {
        val price = price.toDouble()
        val taxPercentage = taxPercentage.toDouble()
        val tax = (taxPercentage * price) / 100
        return Product(
            id = id,
            code = code,
            name = name,
            quantity = quantity,
            price = price,
            currency = currency,
            taxPercentage = taxPercentage,
            tax = tax,
            totalPrice = price + tax,
            tags = tags.map { it.toTag() },
            inactive = inactive,
        )
    }
}
