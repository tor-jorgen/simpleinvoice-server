package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.model.Person
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
    val tags: List<TagRequest>,
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
            tags = tags.map { it.toTag(it.id!!) },
            inactive = inactive,
        )
}

@Serializable
data class PersonRequest(
    @Serializable(with = UUIDSerializer::class) val id: UUID? = null,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email_address") val emailAddress: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
) {
    fun toPerson() =
        Person(
            // Create an ID if this is a new person
            id = id ?: UUID.randomUUID(),
            firstName = firstName,
            lastName = lastName,
            emailAddress = emailAddress,
            phoneNumber = phoneNumber,
        )
}
