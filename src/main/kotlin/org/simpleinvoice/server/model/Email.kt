package org.simpleinvoice.server.model

data class Email(
    val subject: String,
    val text: String? = null,
)
