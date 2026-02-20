package org.simpleinvoice.server.model

import java.util.UUID

data class Household(
    val id: UUID,
    val name: String? = null,
    val address: String,
    val address2: String? = null,
    val zipCode: String,
    val city: String,
    val country: String? = null,
    val persons: List<Person>,
    val tags: List<Tag> = emptyList(),
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

    fun equalsIgnoreIdPersonsAndTags(other: Household): Boolean =
        name == other.name &&
            address == other.address &&
            address2 == other.address2 &&
            zipCode == other.zipCode &&
            city == other.city &&
            country == other.country
}
