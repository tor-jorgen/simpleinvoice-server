package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.model.Person
import java.util.UUID

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
