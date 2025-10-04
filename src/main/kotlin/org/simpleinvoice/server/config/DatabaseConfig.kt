package org.simpleinvoice.server.config

data class DatabaseConfig(
    val connectionString: String,
    val user: String,
    val password: String,
)
