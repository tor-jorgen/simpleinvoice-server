package org.simpleinvoice.server.invoice

import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceLine
import org.simpleinvoice.server.model.InvoiceStatus
import org.simpleinvoice.server.model.Product
import org.simpleinvoice.server.repository.HouseholdRepository
import org.simpleinvoice.server.repository.InvoiceRepository
import org.simpleinvoice.server.repository.ProductRepository
import org.simpleinvoice.server.resources.model.GenerateInvoicesRequest
import java.time.Instant
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

class InvoiceGenerator(
    val productRepository: ProductRepository,
    val householdRepository: HouseholdRepository,
    val invoiceRepository: InvoiceRepository,
    val documentGenerator: DocumentGenerator,
    val emailGenerator: EmailGenerator,
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun generate(request: GenerateInvoicesRequest): List<UUID> {
        val productIds = request.invoiceLines.map { UUID.fromString(it.productId.toString()) }.toList()
        val products = productRepository.byIds(productIds).products.associateBy { it.id }
        return request.householdIds
            .map { householdId ->
                generateInvoice(
                    householdId = UUID.fromString(householdId.toString()),
                    request = request,
                    products = products,
                )
            }.toList()
    }

    private suspend fun generateInvoice(
        householdId: UUID,
        request: GenerateInvoicesRequest,
        products: Map<UUID, Product>,
    ): UUID =
        Invoice(
            id = UUID.randomUUID(),
            invoiceNumber = 0, // New invoice number will be generated
            status = request.status,
            generatedDate = Instant.now(),
            dueDate = request.dueDate,
            finalizedDate = null,
            totalPrice = request.totalPrice,
            currency = request.currency,
            household = householdRepository.get(householdId),
            invoiceLines =
                request.invoiceLines.map {
                    // We know that `products` contains all the products in `invoiceLines`
                    val product = products[it.productId]!! // NOSONAR
                    InvoiceLine(
                        id = UUID.randomUUID(),
                        lineNumber = it.lineNumber,
                        quantity = it.quantity,
                        totalPrice = it.totalPrice,
                        currency = it.currency,
                        product =
                            Product(
                                id = product.id,
                                code = product.code,
                                name = product.name,
                                quantity = product.quantity,
                                price = product.price,
                                currency = product.currency,
                            ),
                    )
                },
        ).let { invoice ->
            val invoiceNumber = invoiceRepository.upsert(invoice = invoice, new = true)
            val invoiceDb = invoice.copy(invoiceNumber = invoiceNumber)
            val (invoiceName, odtPath, pdfPath) = documentGenerator.createDocuments(invoiceDb)
            emailGenerator
                .sendEmail(
                    invoice = invoice,
                    invoiceName = invoiceName,
                    odtPath = odtPath,
                    pdfPath = pdfPath,
                ).let { emailSent ->
                    if (emailSent) {
                        updateStatus(invoice = invoice, status = InvoiceStatus.DELIVERED)
                    }
                }
            invoice.id
        }

    private suspend fun updateStatus(
        invoice: Invoice,
        status: InvoiceStatus,
    ) = invoice.copy(status = status).let { copy ->
        invoiceRepository.upsert(copy, new = false)
    }
}
