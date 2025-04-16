package org.simpleinvoice.server.routing

import com.example.org.simpleinvoice.repository.HouseholdRepository
import com.example.org.simpleinvoice.resources.Households
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import org.simpleinvoice.resources.HouseholdRequest
import java.util.UUID

/**
 * These routes require a valid session, otherwise you have to log in
 */
fun Application.configureHouseholdsRouting() {
    val repository = HouseholdRepository() // TODO: Inject

    routing {
//        authenticate(AUTH_SESSION) {
        get<Households> {
            // Get all Households
            call.respond(repository.all())
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
            call.respondText("$householdRequest with id ${request.id} updated", status = HttpStatusCode.OK)
        }

//        delete<Household.Id> { request ->
//            // Delete a customer
//            call.respondText("A customer with id ${request.id} deleted", status = HttpStatusCode.OK)
//        }
    }
//    }
}
