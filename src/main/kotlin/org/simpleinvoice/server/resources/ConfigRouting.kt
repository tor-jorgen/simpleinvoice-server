package org.simpleinvoice.server.resources

import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.repository.ConfigRepository
import org.simpleinvoice.server.resources.model.ConfigRequest
import java.util.UUID
import org.koin.ktor.ext.get as getK

@Resource("/config")
class Configs {
    @Resource("{id}")
    class Id(
        @Suppress("unused") val parent: Configs = Configs(),
        @Serializable(with = UUIDSerializer::class) val id: UUID,
    )
}

/**
 * These routes require a valid session, otherwise you have to log in
 */
fun Application.configureConfigsRouting(repository: ConfigRepository = getK<ConfigRepository>()) {

    routing {
//        authenticate(AUTH_SESSION) {
        get<Configs> {
            // Get the config
            call.respond(status = HttpStatusCode.OK, message = repository.get())
        }

        put<Configs.Id> { request ->
            // Update a product
            val configRequest = call.receive<ConfigRequest>()
            val config = configRequest.toConfig(request.id)
            repository.update(config)
            call.respond(status = HttpStatusCode.OK, message = config)
        }
    }
//    }
}
