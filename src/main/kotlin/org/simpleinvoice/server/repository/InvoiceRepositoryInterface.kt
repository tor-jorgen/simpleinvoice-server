package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.Invoice
import java.util.UUID

interface InvoiceRepositoryInterface {
    suspend fun all(
        activeOnly: Boolean,
        ids: List<UUID>,
    ): List<Invoice>

    suspend fun get(id: UUID): Invoice

    /**
     * Insert or update an [invoice]. Set [new] to true if this is a new invoice. If set, [message] will be sent to the
     * audit trail
     *
     * Return the invoice number of the inserted or updated invoice.
     */
    suspend fun upsert(
        invoice: Invoice,
        new: Boolean,
        message: String? = null,
    ): Invoice

    /**
     * Delete an invoice with id [id]. All invoice lines will also be deleted
     */
    suspend fun delete(id: UUID): Boolean

    fun nextInvoiceNumber(): Int?
}
