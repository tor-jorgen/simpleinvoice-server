package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.model.Person
import java.util.UUID

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
