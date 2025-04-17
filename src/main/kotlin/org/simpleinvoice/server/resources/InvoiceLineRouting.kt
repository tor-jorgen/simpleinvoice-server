package org.simpleinvoice.server.resources

import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.repository.InvoiceLineRepository
import java.util.UUID

@Resource("/invoicelines")
class InvoiceLines {
    @Resource("{id}")
    class Id(
        val parent: InvoiceLines = InvoiceLines(),
        @Serializable(with = UUIDSerializer::class) val id: UUID,
    )
}

/**
 * These routes require a valid session, otherwise you have to log in
 */
fun Application.configureInvoiceLineRouting() {
    val repository = InvoiceLineRepository() // TODO: Inject

    routing {
        //        authenticate(AUTH_SESSION) {
        get<InvoiceLines> {
            // Get all invoice lines
            call.respond(status = HttpStatusCode.OK, message = repository.all())
        }
    }
    //    }
}
