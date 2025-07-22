package org.simpleinvoice.server.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import java.util.UUID

@Serializable
data class Product(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val code: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val currency: Currency,
    @SerialName("tax_percentage") val taxPercentage: Int,
    @SerialName("total_price") val totalPrice: Double,
    val tags: List<Tag> = emptyList(),
    val inactive: Boolean = false,
)
