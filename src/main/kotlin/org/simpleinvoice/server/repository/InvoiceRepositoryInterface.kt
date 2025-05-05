package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.resources.model.InvoicesResponse
import java.util.UUID

interface InvoiceRepositoryInterface {
    suspend fun all(
        openOnly: Boolean,
        ids: List<UUID>,
    ): InvoicesResponse

    suspend fun get(id: UUID): Invoice

    /**
     * Insert or update an invoice [invoice]. Set [new] to true if this is a new invoice.
     *
     * Return the invoice number of the inserted or updated invoice.
     */
    suspend fun upsert(
        invoice: Invoice,
        new: Boolean,
    ): Invoice

    /**
     * Delete an invoice with id [id]. All invoice lines will also be deleted
     */
    suspend fun delete(id: UUID): Boolean

    fun nextInvoiceNumber(): Int?
}
