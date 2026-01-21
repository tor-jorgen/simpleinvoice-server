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
import org.simpleinvoice.server.invoice.HouseholdImporter
import org.simpleinvoice.server.repository.HouseholdRepository
import org.simpleinvoice.server.resources.model.HouseholdRequest
import org.simpleinvoice.server.resources.model.ImportHouseholdsRequest
import org.simpleinvoice.server.resources.model.ImportHouseholdsResponse
import java.util.UUID
import org.koin.ktor.ext.get as getK

@Resource("/households")
class Households(
    @SerialName("active_only") val activeOnly: Boolean = false,
) {
    @Resource("{id}")
    class Id(
        @Suppress("unused") val parent: Households = Households(),
        @Serializable(with = UUIDSerializer::class) val id: UUID,
    )

    @Resource("import")
    class Import(
        @Suppress("unused") val parent: Households = Households(),
    )
}

/**
 * These routes require a valid session, otherwise you have to log in
 */
@kotlinx.serialization.ExperimentalSerializationApi
fun Application.configureHouseholdsRouting(
    repository: HouseholdRepository = getK<HouseholdRepository>(),
    importer: HouseholdImporter = getK<HouseholdImporter>(),
) {
    routing {
//        authenticate(AUTH_SESSION) {
        get<Households> {
            // Get all Households
            val ids = call.queryParameters["ids"]
            val idList =
                if (ids.isNullOrEmpty()) {
                    emptyList()
                } else {
                    ids.split(",").map { UUID.fromString(it.trim()) }
                }
            val activeOnly = (call.queryParameters["active_only"] ?: "false").toBoolean()
            call.respond(status = HttpStatusCode.OK, message = repository.all(activeOnly = activeOnly, ids = idList))
        }

        post<Households> {
            // Add a new household
            val householdRequest = call.receive<HouseholdRequest>()
            val household = householdRequest.toHousehold(UUID.randomUUID())
            val dbHousehold = repository.upsert(household = household, new = true)
            call.respond(status = HttpStatusCode.Created, message = dbHousehold)
        }

        put<Households.Id> { request ->
            // Update a household with upserts on persons
            val householdRequest = call.receive<HouseholdRequest>()
            val household = householdRequest.toHousehold(request.id)
            val dbHousehold = repository.upsert(household = household, new = false)
            call.respond(status = HttpStatusCode.OK, message = dbHousehold)
        }

        delete<Households.Id> { request ->
            // Delete a household
            repository.delete(request.id)
            call.respond(HttpStatusCode.NoContent)
        }

        post<Households.Import> {
            // Import households
            val households = call.receive<ImportHouseholdsRequest>()
            val householdIds = importer.importHouseholds(households)
            val response = ImportHouseholdsResponse.fromUUIDs(householdIds)
            call.respond(status = HttpStatusCode.OK, message = response)
        }
    }
}
