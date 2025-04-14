package org.simpleinvoice.server.config

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        // JSON with Jackson
        json()
//        jackson {
//            enable(SerializationFeature.INDENT_OUTPUT)
//        }
    }
}
