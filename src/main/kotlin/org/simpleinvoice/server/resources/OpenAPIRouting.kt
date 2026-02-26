package org.simpleinvoice.server.resources

import io.ktor.openapi.OpenApiInfo
import io.ktor.server.application.Application
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.routing.openapi.OpenApiDocSource
import io.ktor.server.routing.routing
import io.ktor.server.routing.routingRoot

fun Application.configureOpenAPIRouting() {
    routing {
        openAPI(path = "openapi") {
            info = OpenApiInfo("Simple Invoice Server", "1.0.3")
            source =
                OpenApiDocSource.Routing {
                    routingRoot.descendants()
                }
        }
    }
}
