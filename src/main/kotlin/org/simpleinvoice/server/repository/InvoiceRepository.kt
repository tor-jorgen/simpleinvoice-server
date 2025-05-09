package org.simpleinvoice.server.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.upsert
import org.simpleinvoice.server.invoice.EventPublisher
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceLine
import org.simpleinvoice.server.model.InvoiceStatus
import org.simpleinvoice.server.repository.model.InvoiceDAO
import org.simpleinvoice.server.repository.model.InvoiceLineTable
import org.simpleinvoice.server.repository.model.InvoiceTable
import org.simpleinvoice.server.resources.model.InvoicesResponse
import java.time.Instant
import java.util.UUID

class InvoiceRepository(
    private val invoiceLineRepository: InvoiceLineRepository,
    private val settingsRepository: SettingsRepository,
    private val eventPublisher: EventPublisher,
) : InvoiceRepositoryInterface {
    override suspend fun all(
        openOnly: Boolean,
        ids: List<UUID>,
    ): InvoicesResponse =
        suspendTransaction {
            InvoicesResponse(
                invoices =
                    if (openOnly) {
                        InvoiceDAO.find { InvoiceTable.status eq InvoiceStatus.DELIVERED.name }.map { it.toInvoice() }
                    } else if (ids.isNotEmpty()) {
                        InvoiceDAO.find { InvoiceTable.id inList ids }.map { it.toInvoice() }
                    } else {
                        InvoiceDAO.all().map { it.toInvoice() }
                    },
            )
        }

    override suspend fun get(id: UUID): Invoice =
        suspendTransaction {
            InvoiceDAO.findById(id)?.toInvoice() ?: throw Exception("Invoice with id: $id not found")
        }

    override suspend fun upsert(
        invoice: Invoice,
        new: Boolean,
    ): Invoice {
        val response =
            suspendTransaction {
                // Delete all invoice lines for the invoice, since we don't know if any have been removed
                InvoiceLineTable.deleteWhere { invoiceId eq invoice.id }
                val dbInvoice =
                    if (new) {
                        // Generate a new invoice number
                        invoice.copy(
                            invoiceNumber =
                                nextInvoiceNumber()
                                    ?: (settingsRepository.getWithoutTransaction().lastInvoiceNumber + 1),
                        )
                    } else {
                        invoice
                    }
                val upsert = upsertWithoutTransaction(dbInvoice)
                invoice.invoiceLines.forEach { invoiceLine ->
                    invoiceLineRepository.upsertWithoutTransaction(invoiceLine, dbInvoice)
                }
                upsert
            }
        eventPublisher.publishEvent(
            id = invoice.id,
            item = invoice,
            message = if (new) "Invoice created" else "Invoice updated",
        )
        return toInvoice(statement = response, household = invoice.household, invoiceLines = invoice.invoiceLines)
    }

    private fun upsertWithoutTransaction(invoice: Invoice): UpsertStatement<Long> =
        InvoiceTable.upsert(onUpdateExclude = listOf(InvoiceTable.invoiceNumber, InvoiceTable.generatedDate)) {
            it[id] = invoice.id
            it[invoiceNumber] = invoice.invoiceNumber
            it[status] = invoice.status.name
            it[generatedDate] = invoice.generatedDate.toString()
            it[dueDate] = invoice.dueDate.toString()
            it[finalizedDate] = invoice.finalizedDate?.toString() ?: kotlin.run { null }
            it[householdId] = invoice.household.id
            it[totalPrice] = invoice.totalPrice
            it[currency] = invoice.currency.name
            it[invoiceFilePath] = invoice.invoiceFilePath
        }

    override suspend fun delete(id: UUID): Boolean {
        val response =
            suspendTransaction {
                InvoiceLineTable.deleteWhere { invoiceId eq id }
                val rowsDeleted = InvoiceTable.deleteWhere { InvoiceTable.id eq id }
                rowsDeleted == 1
            }
        eventPublisher.publishIdEvent(id = id, message = "Invoice deleted")
        return response
    }

    override fun nextInvoiceNumber(): Int? = InvoiceDAO.all().maxOfOrNull { it.invoiceNumber }?.plus(1)

    private fun toInvoice(
        statement: UpsertStatement<Long>,
        household: Household,
        invoiceLines: List<InvoiceLine>,
    ): Invoice =
        Invoice(
            id = statement[InvoiceTable.id].value,
            invoiceNumber = statement[InvoiceTable.invoiceNumber],
            status = InvoiceStatus.valueOf(statement[InvoiceTable.status]),
            generatedDate = Instant.parse(statement[InvoiceTable.generatedDate]),
            dueDate = Instant.parse(statement[InvoiceTable.dueDate]),
            finalizedDate = statement[InvoiceTable.finalizedDate]?.let { Instant.parse(it) },
            invoiceLines = invoiceLines,
            totalPrice = statement[InvoiceTable.totalPrice],
            currency = Currency.valueOf(statement[InvoiceTable.currency]),
            invoiceFilePath = statement[InvoiceTable.invoiceFilePath],
            household = household,
        )
}
