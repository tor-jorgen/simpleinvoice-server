package org.simpleinvoice.server.model

import java.util.UUID

data class User(
    val id: UUID,
    val principalId: String,
    val loginProvider: LoginProvider,
    val firstName: String,
    val lastName: String,
    val emailAddress: String,
    val scopes: Set<String>,
    val inactive: Boolean,
)
