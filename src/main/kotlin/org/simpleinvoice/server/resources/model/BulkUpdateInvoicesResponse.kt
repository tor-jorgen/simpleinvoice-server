package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.model.Invoice
import kotlin.uuid.ExperimentalUuidApi

@Serializable
data class BulkUpdateInvoicesResponse
    @OptIn(ExperimentalUuidApi::class)
    constructor(
        // Uses Uuid to be able to serialize a list of UUIDs
        @SerialName("updated_invoices") val updatedInvoices: List<InvoiceResponse>,
        @SerialName("failed_invoices") val failedInvoices: List<InvoiceResponse>,
        @SerialName("skipped_invoices") val skippedInvoices: List<InvoiceResponse>,
    ) {
        companion object {
            @OptIn(ExperimentalUuidApi::class)
            fun fromUpdate(
                updatedInvoices: List<Invoice>,
                failedInvoices: List<Invoice>,
                skippedInvoices: List<Invoice>,
            ): BulkUpdateInvoicesResponse =
                BulkUpdateInvoicesResponse(
                    updatedInvoices = updatedInvoices.map { InvoiceResponse.fromInvoice(it) },
                    failedInvoices = failedInvoices.map { InvoiceResponse.fromInvoice(it) },
                    skippedInvoices = skippedInvoices.map { InvoiceResponse.fromInvoice(it) },
                )
        }
    }
