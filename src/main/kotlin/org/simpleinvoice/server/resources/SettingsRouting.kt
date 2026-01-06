@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

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
import org.simpleinvoice.server.repository.SettingsRepository
import org.simpleinvoice.server.resources.model.SettingsRequest
import java.util.UUID
import org.koin.ktor.ext.get as getK

@Resource("/settings")
class Settings {
    @Resource("{id}")
    class Id(
        @Suppress("unused") val parent: Settings = Settings(),
        @Serializable(with = UUIDSerializer::class) val id: UUID,
    )
}

/**
 * These routes require a valid session, otherwise you have to log in
 */
fun Application.configureSettingsRouting(repository: SettingsRepository = getK<SettingsRepository>()) {
    routing {
//        authenticate(AUTH_SESSION) {
        get<Settings> {
            // Get the config
            val message = repository.get()
            call.respond(status = HttpStatusCode.OK, message = message)
        }

        put<Settings.Id> { request ->
            // Update a product
            val settingsRequest = call.receive<SettingsRequest>()
            val config = settingsRequest.toSettings(request.id)
            repository.update(config)
            call.respond(status = HttpStatusCode.OK, message = config)
        }
    }
//    }
}
