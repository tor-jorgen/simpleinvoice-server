package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.resources.model.InvoicesResponse
import java.util.UUID

interface InvoiceRepositoryInterface {
    suspend fun all(openOnly: Boolean): InvoicesResponse

    suspend fun upsert(invoice: Invoice): Int

    /**
     * Delete an invoice with id [id]. All invoice lines will also be deleted
     */
    suspend fun delete(id: UUID): Boolean

    fun nextInvoiceNumber(): Int?
}
