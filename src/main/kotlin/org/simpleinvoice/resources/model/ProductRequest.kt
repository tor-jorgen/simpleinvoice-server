package com.example.org.simpleinvoice.resources.model

import kotlinx.serialization.Serializable
import org.simpleinvoice.model.Currency
import org.simpleinvoice.model.Product
import java.util.UUID

@Serializable
class ProductRequest(
    val code: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val currency: Currency,
) {
    fun toProduct(id: UUID): Product =
        Product(
            id = id,
            code = code,
            name = name,
            quantity = quantity,
            price = price,
            currency = currency,
        )
}
