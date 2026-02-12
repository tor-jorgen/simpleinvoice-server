package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.model.Person
import java.util.UUID

@Serializable
data class HouseholdResponse(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val name: String? = null,
    val address: String,
    val address2: String? = null,
    @SerialName("zip_code") val zipCode: String,
    val city: String,
    val country: String? = null,
    val persons: List<PersonResponse>,
    val tags: List<TagRequestResponse> = emptyList(),
    val inactive: Boolean = false,
) {
    companion object {
        fun fromHousehold(household: Household) =
            HouseholdResponse(
                id = household.id,
                name = household.name,
                address = household.address,
                address2 = household.address2,
                zipCode = household.zipCode,
                city = household.city,
                country = household.country,
                persons = household.persons.map { PersonResponse.fromPerson(it) },
                tags = household.tags.map { TagRequestResponse.fromTag(it) },
                inactive = household.inactive,
            )
    }
}

@Serializable
data class PersonResponse(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email_address") val emailAddress: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
) {
    companion object {
        fun fromPerson(person: Person) =
            PersonResponse(
                id = person.id,
                firstName = person.firstName,
                lastName = person.lastName,
                emailAddress = person.emailAddress,
                phoneNumber = person.phoneNumber,
            )
    }
}
