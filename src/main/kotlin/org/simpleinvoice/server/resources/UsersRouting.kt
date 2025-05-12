package org.simpleinvoice.server.resources

import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.repository.UserRepository
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

//        post<Persons> {
//            // Add a new customer
//            val customerRequest = call.receive<CustomerRequest>()
//            val customer = customerRequest.toCustomer(UUID.randomUUID())
//            personRepository.add(customer)
//            call.respond(status = HttpStatusCode.Created, message = customer)
//        }
//
//        get<Persons.Id> { request ->
//            // Show a customer with id ${customer.id}
//            call.respondText("An article with id ${request.id} is fetched", status = HttpStatusCode.OK)
//        }
//
//        put<Persons.Id> { request ->
//            // Update a customer
//            val customer = call.receive<CustomerRequest>()
//            call.respondText("$customer with id ${request.id} updated", status = HttpStatusCode.OK)
//        }
//
//        delete<Persons.Id> { request ->
//            // Delete a customer
//            call.respondText("A customer with id ${request.id} deleted", status = HttpStatusCode.OK)
//        }
    }
    //    }
}
