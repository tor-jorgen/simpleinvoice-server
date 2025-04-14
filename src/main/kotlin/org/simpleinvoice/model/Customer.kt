package com.example.org.simpleinvoice.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Customer(
    val id: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email_address") val emailAddress: String,
    @SerialName("phone_number") val phoneNumber: String? = null,
    val address: String,
    @SerialName("zip_code") val zipCode: String,
    val city: String,
)
