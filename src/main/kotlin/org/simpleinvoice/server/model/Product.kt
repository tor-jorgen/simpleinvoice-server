package org.simpleinvoice.server.model

import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import java.util.UUID

@Serializable
class Product(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val code: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val currency: Currency,
)
