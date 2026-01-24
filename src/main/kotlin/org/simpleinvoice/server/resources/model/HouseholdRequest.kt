package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.model.Household
import java.util.UUID

@Serializable
data class HouseholdRequest(
    val name: String? = null,
    val address: String,
    val address2: String? = null,
    @SerialName("zip_code") val zipCode: String,
    val city: String,
    val country: String? = null,
    val persons: List<PersonRequest>,
    val tags: List<TagRequestResponse> = emptyList(),
    val inactive: Boolean,
) {
    fun toHousehold(id: UUID): Household =
        Household(
            id = id,
            name = name,
            address = address,
            address2 = address2,
            zipCode = zipCode,
            city = city,
            country = country,
            persons = persons.map { it.toPerson() },
            tags = tags.map { it.toTag() },
            inactive = inactive,
        )
}
