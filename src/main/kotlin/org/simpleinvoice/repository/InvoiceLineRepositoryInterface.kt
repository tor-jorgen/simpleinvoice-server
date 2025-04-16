package com.example.org.simpleinvoice.repository

import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.simpleinvoice.model.Invoice
import org.simpleinvoice.model.InvoiceLine
import java.util.UUID

interface InvoiceLineRepositoryInterface {
    suspend fun all(): List<InvoiceLine>

    suspend fun upsert(
        invoiceLine: InvoiceLine,
        invoice: Invoice,
    ): UpsertStatement<Long>

    fun upsertWithoutTransaction(
        invoiceLine: InvoiceLine,
        invoice: Invoice,
    ): UpsertStatement<Long>

    suspend fun delete(id: UUID): Boolean
}
