package org.simpleinvoice.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.common.UUIDSerializer
import org.simpleinvoice.model.Household
import org.simpleinvoice.model.Person
import java.util.UUID

@Serializable
data class HouseholdRequest(
    val address: String,
    @SerialName("zip_code") val zipCode: String,
    val city: String,
    val country: String?,
    val persons: List<PersonRequest>,
) {
    fun toHousehold(id: UUID): Household =
        Household(
            id = id,
            address = address,
            zipCode = zipCode,
            city = city,
            country = country,
            persons = persons.map { it.toPerson() },
        )
}

@Serializable
data class PersonRequest(
    @Serializable(with = UUIDSerializer::class) val id: UUID? = null,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email_address") val emailAddress: String,
    @SerialName("phone_number") val phoneNumber: String? = null,
) {
    fun toPerson() =
        Person(
            id = id ?: UUID.randomUUID(), // Create an ID if this is a new person
            firstName = firstName,
            lastName = lastName,
            emailAddress = emailAddress,
            phoneNumber = phoneNumber,
        )
}
