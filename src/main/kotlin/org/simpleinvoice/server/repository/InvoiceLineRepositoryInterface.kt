package org.simpleinvoice.server.repository

import org.jetbrains.exposed.v1.core.statements.UpsertStatement
import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceLine
import java.util.UUID

interface InvoiceLineRepositoryInterface {
    suspend fun all(): List<InvoiceLine>

    fun upsertWithoutTransaction(
        invoiceLine: InvoiceLine,
        invoice: Invoice,
    ): UpsertStatement<Long>

    suspend fun delete(id: UUID): Boolean
}
