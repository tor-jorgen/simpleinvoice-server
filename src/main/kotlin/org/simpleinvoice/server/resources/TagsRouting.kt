package org.simpleinvoice.server.resources

import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.repository.TagRepository
import org.simpleinvoice.server.resources.model.TagRequest
import java.util.UUID
import org.koin.ktor.ext.get as getK

@Resource("/tags")
class Tags(
    @SerialName("active_only") val activeOnly: Boolean = false,
) {
    @Resource("{id}")
    class Id(
        @Suppress("unused") val parent: Tags = Tags(),
        @Serializable(with = UUIDSerializer::class) val id: UUID,
    )
}

/**
 * These routes require a valid session, otherwise you have to log in
 */
fun Application.configureTagsRouting(repository: TagRepository = getK<TagRepository>()) {
    routing {
//        authenticate(AUTH_SESSION) {
        get<Tags> {
            // Get all tags
            val activeOnly = (call.queryParameters["active_only"] ?: "false").toBoolean()
            call.respond(status = HttpStatusCode.OK, message = repository.all(activeOnly = activeOnly))
        }

        post<Tags> {
            // Create a new tag
            val tagRequest = call.receive<TagRequest>()
            val tag = tagRequest.toTag(UUID.randomUUID())
            repository.upsert(tag = tag, new = true)
            call.respond(status = HttpStatusCode.Created, message = tag)
        }

        put<Tags.Id> { request ->
            // Update a tag
            val tagRequest = call.receive<TagRequest>()
            val tag = tagRequest.toTag(request.id)
            repository.upsert(tag = tag, new = false)
            call.respond(status = HttpStatusCode.OK, message = tag)
        }

        delete<Tags.Id> { request ->
            // Delete a tag
            repository.delete(request.id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
//    }
}
