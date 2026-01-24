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
    @SerialName("total_price") val totalPrice: String,
    val tags: List<TagRequestResponse> = emptyList(),
    val inactive: Boolean,
) {
    fun toProduct(id: UUID): Product =
        Product(
            id = id,
            code = code,
            name = name,
            quantity = quantity,
            price = price.toDouble(),
            currency = currency,
            taxPercentage = taxPercentage.toDouble(),
            totalPrice = totalPrice.toDouble(),
            tags = tags.map { it.toTag() },
            inactive = inactive,
        )
}
