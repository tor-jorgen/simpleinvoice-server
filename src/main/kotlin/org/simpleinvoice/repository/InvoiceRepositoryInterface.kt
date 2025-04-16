package com.example.org.simpleinvoice.repository

import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.simpleinvoice.model.Invoice
import java.util.UUID

interface InvoiceRepositoryInterface {
    suspend fun all(): List<Invoice>

    suspend fun upsert(invoice: Invoice): UpsertStatement<Long>

    /**
     * Delete an invoice with id [id]. All invoice lines will also be deleted
     */
    suspend fun delete(id: UUID): Boolean
}
