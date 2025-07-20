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
import java.util.UUID
import org.koin.ktor.ext.get as getK

@Resource("/users")
class Users {
    @Resource("{id}")
    class Id(
        @Suppress("unused") val parent: Users = Users(),
        @Serializable(with = UUIDSerializer::class) val id: UUID,
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
            repository.upsert(user = user, new = true)
            call.respond(status = HttpStatusCode.Created, message = user)
        }

//        get<Users.Id> { request ->
//            // Show a customer with id ${customer.id}
//            call.respondText("An article with id ${request.id} is fetched", status = HttpStatusCode.OK)
//        }

        put<Users.Id> { request ->
            // Update a user
            val userRequest = call.receive<UserRequest>()
            val user = userRequest.toUser(request.id)
            repository.upsert(user = user, new = false)
            call.respond(status = HttpStatusCode.OK, message = user)
        }

        delete<Users.Id> { request ->
            // Delete a user
            repository.delete(request.id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
    //    }
}
