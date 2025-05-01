package org.simpleinvoice.server.resources

import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceLine
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
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun generate(request: GenerateInvoicesRequest) {
        val productIds = request.invoiceLines.map { UUID.fromString(it.productId.toString()) }.toList()
        val products = productRepository.byIds(productIds).products.associateBy { it.id }
        request.householdIds.forEach { householdId ->
            generateInvoice(
                householdId = UUID.fromString(householdId.toString()),
                request = request,
                products = products,
            )
        }
    }

    suspend fun generateInvoice(
        householdId: UUID,
        request: GenerateInvoicesRequest,
        products: Map<UUID, Product>,
    ): Int =
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
            invoiceRepository.upsert(invoice = invoice, new = true)
        }
}
