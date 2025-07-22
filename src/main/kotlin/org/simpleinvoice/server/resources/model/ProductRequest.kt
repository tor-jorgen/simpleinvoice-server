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
    val price: Double,
    val currency: Currency,
    @SerialName("tax_percentage") val taxPercentage: Int,
    @SerialName("total_price") val totalPrice: Double,
    val tags: List<TagRequest> = emptyList(),
    val inactive: Boolean,
) {
    fun toProduct(id: UUID): Product =
        Product(
            id = id,
            code = code,
            name = name,
            quantity = quantity,
            price = price,
            currency = currency,
            taxPercentage = taxPercentage,
            totalPrice = totalPrice,
            tags = tags.map { it.toTag(it.id!!) },
            inactive = inactive,
        )
}
