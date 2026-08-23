package org.simpleinvoice.server.resources

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.routing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.invoice.InvoiceBulkUpdater
import org.simpleinvoice.server.invoice.InvoiceConfig
import org.simpleinvoice.server.invoice.InvoiceGenerator
import org.simpleinvoice.server.repository.InvoiceRepository
import org.simpleinvoice.server.resources.model.BulkUpdateInvoicesRequest
import org.simpleinvoice.server.resources.model.BulkUpdateInvoicesResponse
import org.simpleinvoice.server.resources.model.GenerateInvoicesRequest
import org.simpleinvoice.server.resources.model.GenerateInvoicesResponse
import org.simpleinvoice.server.resources.model.InvoiceRequest
import org.simpleinvoice.server.resources.model.InvoiceResponse
import org.simpleinvoice.server.resources.model.ListResponse
import org.simpleinvoice.server.util.s3.StorageClient
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import org.koin.ktor.ext.get as getK

@Resource("/invoices")
class Invoices(
    @SerialName("active_only") val activeOnly: Boolean = false,
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

    @Resource("/bulk")
    class Bulk(
        @Suppress("unused") val parent: Invoices = Invoices(),
    )
}

/**
 * These routes require a valid session, otherwise you have to log in
 */
@OptIn(ExperimentalUuidApi::class)
fun Application.configureInvoicesRouting(
    repository: InvoiceRepository = getK<InvoiceRepository>(),
    invoiceGenerator: InvoiceGenerator = getK<InvoiceGenerator>(),
    invoiceBulkUpdater: InvoiceBulkUpdater = getK<InvoiceBulkUpdater>(),
    invoiceConfig: InvoiceConfig = getK<InvoiceConfig>(),
    storageClient: StorageClient = getK<StorageClient>(),
) {
    routing {
//        authenticate(AUTH_SESSION) {
        get<Invoices> {
            // Get all invoices
            val activeOnly = (call.queryParameters["active_only"] ?: "false").toBoolean()
            val ids = call.queryParameters["ids"]
            val idList =
                if (ids.isNullOrEmpty()) {
                    emptyList()
                } else {
                    ids.split(",").map { UUID.fromString(it.trim()) }
                }
            val response =
                ListResponse(
                    data =
                        repository
                            .all(activeOnly = activeOnly, ids = idList)
                            .map { InvoiceResponse.fromInvoice(it) },
                )
            call.respond(status = HttpStatusCode.OK, message = response)
        }

        get<Invoices.Id.Document> { invoice ->
            // Get the invoice document
            repository.get(invoice.parent.id).let { invoice ->
                if (invoice.invoiceFilePath != null) {
                    val bytes = storageClient.download(invoiceConfig.invoiceBucketName, invoice.invoiceFilePath)
                    call.respondBytes(
                        bytes = bytes,
                        contentType = ContentType.Application.Pdf,
                        status = HttpStatusCode.OK,
                    )
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
            // Invoice number can be set to anything, as it wil be generated for a new invoice
            val invoice = invoiceRequest.toInvoice(id = UUID.randomUUID(), invoiceNumber = 0)
            val response =
                InvoiceResponse.fromInvoice(
                    invoiceGenerator
                        .generate(
                            invoice = invoice,
                            householdIds = listOf(invoice.household.id),
                            email = null,
                            new = true,
                        ).first(),
                )
            call.respond(status = HttpStatusCode.Created, message = response)
        }

        post<Invoices.Generate> {
            // Generate new invoice(s)
            val request = call.receive<GenerateInvoicesRequest>()
            val invoices =
                invoiceGenerator.generate(
                    invoice = request.toInvoice(),
                    householdIds = request.householdIds.map { UUID.fromString(it.toString()) },
                    email = request.email?.toEmail(),
                    new = true,
                )
            val response = GenerateInvoicesResponse.fromInvoices(invoices)
            call.respond(status = HttpStatusCode.OK, message = response)
        }

        post<Invoices.Bulk> {
            // Bulk update invoice(s)
            val request = call.receive<BulkUpdateInvoicesRequest>()
            val result =
                invoiceBulkUpdater.bulkUpdate(
                    invoiceIds = request.invoiceIds.map { UUID.fromString(it.toString()) },
                    status = request.status,
                    message = request.message,
                )
            val response =
                BulkUpdateInvoicesResponse.fromUpdate(
                    updatedInvoices = result.first,
                    failedInvoices = result.second,
                    skippedInvoices = result.third,
                )
            val status = if (response.failedInvoices.isEmpty()) HttpStatusCode.OK else HttpStatusCode.PartialContent
            call.respond(status = status, message = response)
        }

        put<Invoices.Id> { request ->
            // Update an invoice with upserts on invoice lines
            val invoiceRequest = call.receive<InvoiceRequest>()
            // Invoice number can be set to anything, as it wil not be updated for an existing invoice
            val invoice = invoiceRequest.toInvoice(id = request.id, invoiceNumber = 0)
            val response =
                InvoiceResponse.fromInvoice(
                    invoiceGenerator
                        .generate(
                            invoice = invoice,
                            householdIds = listOf(invoice.household.id),
                            email = null,
                            new = false,
                        ).first(),
                )
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
