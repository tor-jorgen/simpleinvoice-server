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
import org.simpleinvoice.server.repository.HouseholdRepository
import org.simpleinvoice.server.resources.model.HouseholdRequest
import java.util.UUID

@Resource("/households")
class Households {
    @Resource("{id}")
    class Id(
        val parent: Households = Households(),
        @Serializable(with = UUIDSerializer::class) val id: UUID,
    )
}

/**
 * These routes require a valid session, otherwise you have to log in
 */
fun Application.configureHouseholdsRouting() {
    val repository = HouseholdRepository() // TODO: Inject

    routing {
//        authenticate(AUTH_SESSION) {
        get<Households> {
            // Get all Households
            call.respond(status = HttpStatusCode.OK, message = repository.all())
        }

        post<Households> {
            // Add a new household
            val householdRequest = call.receive<HouseholdRequest>()
            val household = householdRequest.toHousehold(UUID.randomUUID())
            println("doing an upsert on: $household")
            val upsert = repository.upsert(household)
            println(upsert)
            call.respond(status = HttpStatusCode.Created, message = household)
        }

        //        get<Household.Id> { request ->
//            // Show a customer with id ${customer.id}
//            call.respondText("An article with id ${request.id} is fetched", status = HttpStatusCode.OK)
//        }

        put<Households.Id> { request ->
            // Update a household with upserts on persons
            val householdRequest = call.receive<HouseholdRequest>()
            val household = householdRequest.toHousehold(request.id)
            repository.upsert(household)
            call.respond(status = HttpStatusCode.OK, message = household)
        }

        delete<Households.Id> { request ->
            // Delete a household
            repository.delete(request.id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
//    }
}
