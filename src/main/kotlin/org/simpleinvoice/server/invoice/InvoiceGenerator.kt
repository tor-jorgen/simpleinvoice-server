package org.simpleinvoice.server.invoice

import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceLine
import org.simpleinvoice.server.model.InvoiceStatus
import org.simpleinvoice.server.model.Product
import org.simpleinvoice.server.repository.HouseholdRepository
import org.simpleinvoice.server.repository.InvoiceRepository
import org.simpleinvoice.server.repository.ProductRepository
import org.simpleinvoice.server.resources.model.EmailRequest
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
        val products = productRepository.byIds(productIds).associateBy { it.id }
        return request.householdIds
            .map { householdId ->
                generateInvoice(
                    householdId = UUID.fromString(householdId.toString()),
                    request = request,
                    products = products,
                ).id
            }.toList()
    }

    private suspend fun generateInvoice(
        householdId: UUID,
        request: GenerateInvoicesRequest,
        products: Map<UUID, Product>,
    ): Invoice =
        Invoice(
            id = UUID.randomUUID(),
            // New invoice number will be generated
            invoiceNumber = 0,
            status = InvoiceStatus.CREATED,
            generatedDate = Instant.now(),
            dueDate = request.dueDate,
            finalizedDate = null,
            price = request.price,
            tax = request.tax,
            totalPrice = request.totalPrice,
            currency = request.currency,
            household = householdRepository.get(householdId),
            invoiceFilePath = null,
            tags = request.tags.map { it.toTag() },
            invoiceLines =
                request.invoiceLines.map {
                    // We know that `products` contains all the products in `invoiceLines`
                    val product = products[it.productId]!! // NOSONAR
                    InvoiceLine(
                        id = UUID.randomUUID(),
                        lineNumber = it.lineNumber,
                        quantity = it.quantity,
                        price = it.price,
                        tax = it.tax,
                        totalPrice = it.totalPrice,
                        currency = it.currency,
                        product =
                            Product(
                                id = product.id,
                                code = product.code,
                                name = product.name,
                                quantity = product.quantity,
                                price = product.price,
                                taxPercentage = product.taxPercentage,
                                tax = product.tax,
                                totalPrice = product.totalPrice,
                                currency = product.currency,
                                tags = product.tags,
                            ),
                    )
                },
        ).let { invoice -> generate(invoice = invoice, email = request.email, new = true) }

    suspend fun generate(
        invoice: Invoice,
        new: Boolean,
        email: EmailRequest? = null,
    ): Invoice {
        var invoiceDb = invoiceRepository.upsert(invoice = invoice, new = new)
        val (invoiceName, _, pdfPath) = documentGenerator.createDocuments(invoiceDb)
        if (email != null) {
            emailGenerator
                .sendEmail(
                    invoice = invoice,
                    invoiceName = invoiceName,
                    pdfPath = pdfPath,
                    email = email,
                ).let { emailSent ->
                    val status = if (emailSent) InvoiceStatus.DELIVERED else invoice.status
                    invoiceDb = updateInvoice(invoice = invoice, invoiceFilePath = pdfPath, status = status)
                }
        } else {
            invoiceDb = updateInvoice(invoice = invoice, invoiceFilePath = pdfPath)
        }
        return invoiceDb
    }

    private suspend fun updateInvoice(
        invoice: Invoice,
        invoiceFilePath: String,
        status: InvoiceStatus? = null,
    ): Invoice {
        val copy =
            if (status != null) {
                invoice.copy(status = status, invoiceFilePath = invoiceFilePath)
            } else {
                invoice.copy(invoiceFilePath = invoiceFilePath)
            }
        return invoiceRepository.upsert(copy, new = false)
    }
}
