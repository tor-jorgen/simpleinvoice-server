package org.simpleinvoice.model

import com.example.org.simpleinvoice.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class Product(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val code: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val currency: String,
)
