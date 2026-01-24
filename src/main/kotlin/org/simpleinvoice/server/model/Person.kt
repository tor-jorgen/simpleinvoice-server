package org.simpleinvoice.server.model

import java.util.UUID

data class Person(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val emailAddress: String? = null,
    val phoneNumber: String? = null,
)
