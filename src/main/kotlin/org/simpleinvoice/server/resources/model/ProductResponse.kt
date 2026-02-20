package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Product
import java.util.UUID

@Serializable
data class ProductResponse(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val code: String,
    val name: String,
    val quantity: Int,
    val price: String,
    @SerialName("tax_percentage") val taxPercentage: String,
    val tax: String,
    @SerialName("total_price") val totalPrice: String,
    val currency: Currency,
    val tags: List<TagRequestResponse> = emptyList(),
    val inactive: Boolean = false,
) {
    companion object {
        fun fromProduct(product: Product): ProductResponse =
            ProductResponse(
                id = product.id,
                code = product.code,
                name = product.name,
                quantity = product.quantity,
                price = product.price.toString(),
                taxPercentage = product.taxPercentage.toString(),
                tax = product.tax.toString(),
                totalPrice = product.totalPrice.toString(),
                currency = product.currency,
                tags = product.tags.map { TagRequestResponse.fromTag(it) },
                inactive = product.inactive,
            )
    }
}
