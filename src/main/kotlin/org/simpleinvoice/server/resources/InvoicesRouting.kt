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
import org.simpleinvoice.server.repository.InvoiceRepository
import org.simpleinvoice.server.resources.model.InvoiceRequest
import java.util.UUID
import org.koin.ktor.ext.get as getK

@Resource("/invoices")
class Invoices(
    @SerialName("open_only") val openOnly: Boolean = false,
) {
    @Resource("{id}")
    class Id(
        @Suppress("unused") val parent: Invoices = Invoices(),
        @Serializable(with = UUIDSerializer::class) val id: UUID,
    )
}

/**
 * These routes require a valid session, otherwise you have to log in
 */
fun Application.configureInvoicesRouting(repository: InvoiceRepository = getK<InvoiceRepository>()) {
    routing {
//        authenticate(AUTH_SESSION) {
        get<Invoices> {
            // Get all invoices
            val openOnly = (call.queryParameters["open_only"] ?: "false").toBoolean()
            call.respond(status = HttpStatusCode.OK, message = repository.all(openOnly))
        }

        post<Invoices> {
            // Create a new invoice
            val invoiceRequest = call.receive<InvoiceRequest>()
            val invoice = invoiceRequest.toInvoice(UUID.randomUUID())
            repository.upsert(invoice)
            call.respond(status = HttpStatusCode.Created, message = invoice)
        }

        put<Invoices.Id> { request ->
            // Update an invoice with upserts on invoice lines
            val invoiceRequest = call.receive<InvoiceRequest>()
            val invoice = invoiceRequest.toInvoice(request.id)
            repository.upsert(invoice)
            call.respond(status = HttpStatusCode.OK, message = invoice)
        }

        delete<Invoices.Id> { request ->
            // Delete an invoice line
            repository.delete(request.id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
//    }
}
