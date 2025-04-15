package org.simpleinvoice.model

import com.example.org.simpleinvoice.common.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Customer(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email_address") val emailAddress: String,
    @SerialName("phone_number") val phoneNumber: String? = null,
    val address: String,
    @SerialName("zip_code") val zipCode: String,
    val city: String,
)
