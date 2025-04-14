package org.simpleinvoice.server.config

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        // JSON with Jackson
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}
