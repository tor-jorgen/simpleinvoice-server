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
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.repository.UserRepository
import org.simpleinvoice.server.resources.model.UserRequest
import org.simpleinvoice.server.resources.model.UserResponse
import java.util.UUID
import org.koin.ktor.ext.get as getK

@Resource("/users")
class Users {
    @Resource("{id}")
    class Id(
        @Suppress("unused") val parent: Users = Users(),
        @Serializable(with = UUIDSerializer::class) val id: UUID,
        val message: String? = null,
    )
}

/**
 * These routes require a valid session, otherwise you have to log in
 */
fun Application.configurePersonsRouting(repository: UserRepository = getK<UserRepository>()) {
    routing {
        //        authenticate(AUTH_SESSION) {
        get<Users> {
            // Get all persons
            call.respond(repository.all())
        }

        post<Users> {
            // Add a new user
            val request = call.receive<UserRequest>()
            val user = request.toUser(UUID.randomUUID())
            val response = UserResponse.fromUser(repository.upsert(user = user, new = true, message = request.message))
            call.respond(status = HttpStatusCode.Created, message = response)
        }

        put<Users.Id> { request ->
            // Update a user
            val userRequest = call.receive<UserRequest>()
            val user = userRequest.toUser(request.id)
            val response =
                UserResponse.fromUser(repository.upsert(user = user, new = false, message = userRequest.message))
            call.respond(status = HttpStatusCode.OK, message = response)
        }

        delete<Users.Id> { request ->
            // Delete a user
            val message: String? = call.queryParameters["message"]
            repository.delete(request.id, message = message)
            call.respond(HttpStatusCode.NoContent)
        }
    }
    //    }
}
