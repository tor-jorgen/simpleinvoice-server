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
import io.ktor.server.response.respondFile
import io.ktor.server.routing.routing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.invoice.InvoiceGenerator
import org.simpleinvoice.server.repository.HouseholdRepository
import org.simpleinvoice.server.repository.InvoiceRepository
import org.simpleinvoice.server.repository.ProductRepository
import org.simpleinvoice.server.resources.model.GenerateInvoicesRequest
import org.simpleinvoice.server.resources.model.GenerateInvoicesResponse
import org.simpleinvoice.server.resources.model.InvoiceRequest
import org.simpleinvoice.server.resources.model.InvoiceResponse
import org.simpleinvoice.server.resources.model.InvoicesResponse
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import org.koin.ktor.ext.get as getK

@Resource("/invoices")
class Invoices(
    @SerialName("open_only") val openOnly: Boolean = false,
) {
    @Resource("{id}")
    class Id(
        @Suppress("unused") val parent: Invoices = Invoices(),
        @Serializable(with = UUIDSerializer::class) val id: UUID,
    ) {
        @Resource("document")
        class Document(
            val parent: Id,
        )
    }

    @Resource("/generate")
    class Generate(
        @Suppress("unused") val parent: Invoices = Invoices(),
    )
}

/**
 * These routes require a valid session, otherwise you have to log in
 */
@OptIn(ExperimentalUuidApi::class)
fun Application.configureInvoicesRouting(
    repository: InvoiceRepository = getK<InvoiceRepository>(),
    householdRepository: HouseholdRepository = getK<HouseholdRepository>(),
    productRepository: ProductRepository = getK<ProductRepository>(),
    invoiceGenerator: InvoiceGenerator = getK<InvoiceGenerator>(),
) {
    routing {
//        authenticate(AUTH_SESSION) {
        get<Invoices> {
            // Get all invoices
            val openOnly = (call.queryParameters["open_only"] ?: "false").toBoolean()
            val ids = call.queryParameters["ids"]
            val idList =
                if (ids.isNullOrEmpty()) {
                    emptyList()
                } else {
                    ids.split(",").map { UUID.fromString(it.trim()) }
                }
            val response =
                InvoicesResponse(
                    invoices =
                        repository
                            .all(openOnly = openOnly, ids = idList)
                            .map { InvoiceResponse.fromInvoice(it) },
                )
            call.respond(status = HttpStatusCode.OK, message = response)
        }

        get<Invoices.Id.Document> { invoice ->
            // Get the invoice document
            repository.get(invoice.parent.id).let { invoice ->
                if (invoice.invoiceFilePath != null) {
                    val pdfFile = java.io.File(invoice.invoiceFilePath)
                    if (pdfFile.exists()) {
                        call.respondFile(pdfFile)
                    }
                }
            }
            call.respond(
                HttpStatusCode.NotFound,
                "PDF file for invoice could not be found",
            )
        }

        post<Invoices> {
            // Create a new invoice
            val invoiceRequest = call.receive<InvoiceRequest>()
            val household = householdRepository.get(invoiceRequest.householdId)
            val products =
                productRepository.byIds(invoiceRequest.invoiceLines.map { it.productId }).associateBy { it.id }
            // Invoice number can be set to anything, as it wil be generated for a new invoice
            val invoice =
                invoiceRequest.toInvoice(
                    id = UUID.randomUUID(),
                    invoiceNumber = 0,
                    household = household,
                    products = products,
                )
            val response = InvoiceResponse.fromInvoice(invoiceGenerator.generate(invoice = invoice, new = true))
            call.respond(status = HttpStatusCode.Created, message = response)
        }

        post<Invoices.Generate> {
            // Generate new invoice(s)
            val request = call.receive<GenerateInvoicesRequest>()
            val invoiceIds = invoiceGenerator.generate(request = request)
            val response = GenerateInvoicesResponse.fromUUIDs(invoiceIds)
            call.respond(status = HttpStatusCode.OK, message = response)
        }

        put<Invoices.Id> { request ->
            // Update an invoice with upserts on invoice lines
            val invoiceRequest = call.receive<InvoiceRequest>()
            val household = householdRepository.get(invoiceRequest.householdId)
            val products =
                productRepository.byIds(invoiceRequest.invoiceLines.map { it.productId }).associateBy { it.id }
            // Invoice number can be set to anything, as it wil not be updated for an existing invoice
            val invoice =
                invoiceRequest.toInvoice(id = request.id, invoiceNumber = 0, household = household, products = products)
            val response = InvoiceResponse.fromInvoice(invoiceGenerator.generate(invoice, new = false))
            call.respond(status = HttpStatusCode.OK, message = response)
        }

        delete<Invoices.Id> { request ->
            // Delete an invoice line
            repository.delete(request.id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
//    }
}
