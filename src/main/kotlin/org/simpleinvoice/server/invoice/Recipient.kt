package org.simpleinvoice.server.invoice

data class Recipient(
    val name: String,
    val addressLine1: String,
    val addressLine2: String?,
    val zipCity: String,
    val country: String?,
    val email: String?,
)
