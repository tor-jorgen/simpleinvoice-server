package com.example.org.simpleinvoice.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomerUpdate(
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email_address") val emailAddress: String,
    @SerialName("phone_number") val phoneNumber: String? = null,
    val address: String,
    @SerialName("zip_code") val zipCode: String,
    val city: String,
)
