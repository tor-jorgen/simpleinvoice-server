package org.simpleinvoice.server.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.upsert
import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceStatus
import org.simpleinvoice.server.repository.model.InvoiceDAO
import org.simpleinvoice.server.repository.model.InvoiceLineTable
import org.simpleinvoice.server.repository.model.InvoiceTable
import org.simpleinvoice.server.resources.model.InvoicesResponse
import java.util.UUID

class InvoiceRepository(
    private val invoiceLineRepository: InvoiceLineRepository,
    private val settingsRepository: SettingsRepository,
) : InvoiceRepositoryInterface {
    override suspend fun all(openOnly: Boolean): InvoicesResponse =
        suspendTransaction {
            InvoicesResponse(invoices = InvoiceDAO.all().map { it.toInvoice() })
            InvoicesResponse(
                invoices =
                    if (openOnly) {
                        InvoiceDAO.find { InvoiceTable.status eq InvoiceStatus.DELIVERED.name }.map { it.toInvoice() }
                    } else {
                        InvoiceDAO.all().map { it.toInvoice() }
                    },
            )
        }

    override suspend fun upsert(
        invoice: Invoice,
        new: Boolean,
    ): Int =
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
            upsert.resultedValues!!.first().fieldIndex[InvoiceTable.invoiceNumber]!!
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
        }

    override suspend fun delete(id: UUID): Boolean =
        suspendTransaction {
            InvoiceLineTable.deleteWhere { invoiceId eq id }
            val rowsDeleted = InvoiceTable.deleteWhere { InvoiceTable.id eq id }
            rowsDeleted == 1
        }

    override fun nextInvoiceNumber(): Int? = InvoiceDAO.all().maxOfOrNull { it.invoiceNumber }?.plus(1)
}
