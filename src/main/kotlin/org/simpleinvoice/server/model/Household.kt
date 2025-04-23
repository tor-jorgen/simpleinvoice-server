package org.simpleinvoice.server.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import java.util.UUID

@Serializable
data class Household(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val name: String? = null,
    val address: String,
    @SerialName("zip_code") val zipCode: String,
    val city: String,
    val country: String? = null,
    val persons: List<Person>,
    val inactive: Boolean = false,
)
