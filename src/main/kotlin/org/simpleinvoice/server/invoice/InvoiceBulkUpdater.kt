package org.simpleinvoice.server.invoice

import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceStatus
import org.simpleinvoice.server.repository.InvoiceRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

class InvoiceBulkUpdater(
    val repository: InvoiceRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    suspend fun bulkUpdate(
        invoiceIds: List<UUID>,
        status: InvoiceStatus,
        message: String? = null,
    ): Triple<List<Invoice>, List<Invoice>, List<Invoice>> {
        val invoices = mutableListOf<Invoice>()
        val failedInvoices = mutableListOf<Invoice>()
        val skippedInvoices = mutableListOf<Invoice>()
        // This can be run in parallel, but not worth it for now
        invoiceIds.forEach { invoiceId ->
            try {
                val currentInvoice = repository.get(invoiceId)
                if (currentInvoice.status !== status) {
                    currentInvoice
                        .copy(status = status)
                        .let {
                            invoices.addLast(repository.upsert(invoice = it, new = false, message = message))
                        }
                } else {
                    skippedInvoices.addLast(currentInvoice)
                }
            } catch (e: Exception) {
                logger.error("Failed to update invoice with ID: {}", invoiceId, e)
                try {
                    repository.get(invoiceId).let { failedInvoices.addLast(it) }
                } catch (_: Exception) {
                    failedInvoices.addLast(
                        Invoice(
                            id = invoiceId,
                            invoiceNumber = 0,
                            status = InvoiceStatus.CREATED,
                            generatedDate = Instant.now(),
                            dueDate = Instant.now(),
                            finalizedDate = null,
                            price = 0.0,
                            tax = 0.0,
                            totalPrice = 0.0,
                            currency = Currency.NONE,
                            household =
                                Household(
                                    id = UUID.randomUUID(),
                                    name = "",
                                    address = "",
                                    address2 = null,
                                    zipCode = "",
                                    city = "",
                                    persons = emptyList(),
                                ),
                            invoiceFilePath = null,
                            invoiceLines = emptyList(),
                            tags = emptyList(),
                        ),
                    )
                }
            }
        }

        return Triple(invoices, failedInvoices, skippedInvoices)
    }
}
