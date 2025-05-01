package org.simpleinvoice.server.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import java.util.UUID

@Serializable
data class Person(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email_address") val emailAddress: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
)
