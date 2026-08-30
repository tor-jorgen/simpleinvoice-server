package org.simpleinvoice.server.resources

import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import org.simpleinvoice.server.BuildConfig
import org.simpleinvoice.server.resources.model.InfoResponse

@Resource("/info")
class Info

fun Application.configureInfoRouting() {
    routing {
        get<Info> {
            call.respond(status = HttpStatusCode.OK, message = InfoResponse(version = BuildConfig.VERSION))
        }
    }
}
