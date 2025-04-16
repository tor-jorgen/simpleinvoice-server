package org.simpleinvoice.server.config

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.util.toMap

fun Application.configureCallLogging() {
    install(CallLogging) {
        format { call ->
            val method = call.request.httpMethod.value
            val uri = call.request.uri
            val headers = call.request.headers.toMap()
            val queryParams = call.request.queryParameters.toMap()
            val status = call.response.status() ?: "Unhandled"

            "HTTP $method $uri | Status: $status | Headers: $headers | QueryParams: $queryParams"
        }
    }
}
