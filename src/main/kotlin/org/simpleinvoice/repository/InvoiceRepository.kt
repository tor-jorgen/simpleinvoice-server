package com.example.org.simpleinvoice.repository

import com.example.org.simpleinvoice.repository.model.InvoiceDAO
import com.example.org.simpleinvoice.repository.model.InvoiceTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.upsert
import org.simpleinvoice.model.Invoice
import org.simpleinvoice.repository.model.InvoiceLineTable
import org.simpleinvoice.repository.suspendTransaction
import java.util.UUID

class InvoiceRepository : InvoiceRepositoryInterface {
    private val invoiceLineRepository = InvoiceLineRepository() // TODO: Inject

    override suspend fun all(): List<Invoice> =
        suspendTransaction {
            InvoiceDAO.all().map { it.toInvoice() }
        }

    override suspend fun upsert(invoice: Invoice): UpsertStatement<Long> =
        suspendTransaction {
            val upsert =
                upsertWithoutTransaction(invoice)
            invoice.invoiceLines.forEach { invoiceLine ->
                invoiceLineRepository.upsertWithoutTransaction(invoiceLine, invoice)
            }
            upsert
        }

    private fun upsertWithoutTransaction(invoice: Invoice): UpsertStatement<Long> =
        InvoiceTable.upsert {
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
}
