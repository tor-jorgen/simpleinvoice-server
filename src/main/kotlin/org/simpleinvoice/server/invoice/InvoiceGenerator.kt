package org.simpleinvoice.server.invoice

import org.simpleinvoice.server.model.Email
import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceLine
import org.simpleinvoice.server.model.InvoiceStatus
import org.simpleinvoice.server.repository.HouseholdRepository
import org.simpleinvoice.server.repository.InvoiceRepository
import org.simpleinvoice.server.repository.ProductRepository
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
    suspend fun generate(
        invoice: Invoice,
        householdIds: List<UUID>,
        email: Email?,
        new: Boolean,
    ): List<Invoice> =
        householdIds
            .map { householdId ->
                val calculated =
                    calculateInvoice(invoice = invoice, householdId = householdId, keepIds = householdIds.size == 1)
                generate(invoice = calculated, email = email, new = new)
            }.toList()

    private suspend fun calculateInvoice(
        invoice: Invoice,
        householdId: UUID,
        keepIds: Boolean = false,
    ): Invoice {
        val productIds = invoice.invoiceLines.map { it.product.id }.toList()
        val products = productRepository.byIds(productIds).associateBy { it.id }
        val invoiceLines =
            invoice.invoiceLines.map {
                // We know that `products` contains all the products in `invoiceLines`
                val product = products[it.product.id]!! // NOSONAR
                InvoiceLine(
                    id = if (keepIds) it.id else UUID.randomUUID(),
                    lineNumber = it.lineNumber,
                    quantity = it.quantity,
                    price = product.price * it.quantity,
                    tax = product.tax * it.quantity,
                    totalPrice = (product.price + product.tax) * it.quantity,
                    currency = it.currency,
                    product = product,
                )
            }
        var price = 0.0
        var tax = 0.0
        var totalPrice = 0.0
        invoiceLines.forEach {
            price += it.price
            tax += it.tax
            totalPrice += it.totalPrice
        }
        return Invoice(
            id = if (keepIds) invoice.id else UUID.randomUUID(),
            // New invoice number will be generated/existing will be fetched
            invoiceNumber = 0,
            status = InvoiceStatus.CREATED,
            generatedDate = Instant.now(),
            dueDate = invoice.dueDate,
            finalizedDate = null,
            price = price,
            tax = tax,
            totalPrice = totalPrice,
            currency = invoice.currency,
            household = householdRepository.get(UUID.fromString(householdId.toString())),
            invoiceFilePath = null,
            tags = invoice.tags,
            invoiceLines = invoiceLines,
        )
    }

    private suspend fun generate(
        invoice: Invoice,
        new: Boolean,
        email: Email?,
    ): Invoice {
        var invoiceDb = invoiceRepository.upsert(invoice = invoice, new = new)
        val (invoiceName, pdfPath) = documentGenerator.createDocuments(invoiceDb)
        if (email != null) {
            emailGenerator
                .sendEmail(
                    invoice = invoiceDb,
                    invoiceName = invoiceName,
                    pdfPath = pdfPath,
                    email = email,
                ).let { emailSent ->
                    val status = if (emailSent) InvoiceStatus.DELIVERED else invoiceDb.status
                    invoiceDb = updateInvoice(invoice = invoiceDb, invoiceFilePath = pdfPath, status = status)
                }
        } else {
            invoiceDb = updateInvoice(invoice = invoiceDb, invoiceFilePath = pdfPath)
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
