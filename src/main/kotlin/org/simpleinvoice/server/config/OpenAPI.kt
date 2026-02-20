package org.simpleinvoice.server.config

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.openApi
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureOpenAPI() {
    install(OpenApi)

    routing {
        route("/api.json") {
            // Create a route to expose the OpenAPI specification file at `/api.json`.
            openApi()
        }
    }
}
