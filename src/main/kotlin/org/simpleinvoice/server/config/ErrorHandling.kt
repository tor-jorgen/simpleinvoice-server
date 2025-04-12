package org.simpleinvoice.server.config

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.log.error("Hello, World!", cause)
            call.respondText(
                text = "Internal Server error. See logs for more information",
                status = HttpStatusCode.InternalServerError,
            )
        }
    }
}
