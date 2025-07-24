package org.simpleinvoice.server.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.upsert
import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceLine
import org.simpleinvoice.server.repository.model.InvoiceLineDAO
import org.simpleinvoice.server.repository.model.InvoiceLineTable
import java.util.UUID

class InvoiceLineRepository : InvoiceLineRepositoryInterface {
    override suspend fun all(): List<InvoiceLine> =
        suspendTransaction {
            InvoiceLineDAO.all().map { it.toInvoiceLine() }
        }

    override fun upsertWithoutTransaction(
        invoiceLine: InvoiceLine,
        invoice: Invoice,
    ): UpsertStatement<Long> =
        InvoiceLineTable.upsert {
            it[invoiceId] = invoice.id
            it[lineNumber] = invoiceLine.lineNumber
            it[productId] = invoiceLine.product.id
            it[quantity] = invoiceLine.quantity
            it[price] = invoiceLine.price
            it[tax] = invoiceLine.tax
            it[totalPrice] = invoiceLine.totalPrice
            it[currency] = invoiceLine.currency.name
        }

    override suspend fun delete(id: UUID): Boolean =
        suspendTransaction {
            val rowsDeleted =
                InvoiceLineTable.deleteWhere {
                    InvoiceLineTable.id eq id
                }
            rowsDeleted == 1
        }
}
