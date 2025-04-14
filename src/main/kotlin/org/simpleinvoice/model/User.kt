package com.example.org.simpleinvoice.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class User(
    val id: String,
    @SerialName("principal_id") val principalId: String,
    @SerialName("login_provider") val loginProvider: LoginProvider,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email_address") val emailAddress: String,
    val scopes: Set<String>,
)
