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
    val address2: String? = null,
    @SerialName("zip_code") val zipCode: String,
    val city: String,
    val country: String? = null,
    val persons: List<Person>,
    val inactive: Boolean = false,
) {
    fun description(): String =
        if (!name.isNullOrBlank()) {
            name
        } else {
            // TODO: Set name if not set?
            // TODO: Email should not be blank
            persons.map { it.lastName }.toSet().joinToString(", ")
        }

    fun equalsIgnoreIdAndPersons(other: Household): Boolean =
        name == other.name &&
            address == other.address &&
            address2 == other.address2 &&
            zipCode == other.zipCode &&
            city == other.city &&
            country == other.country
}
