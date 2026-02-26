package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.model.Invoice
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class GenerateInvoicesResponse
    @OptIn(ExperimentalUuidApi::class)
    constructor(
        // Uses Uuid to be able to serialize a list of UUIDs
        @SerialName("invoice_ids") val invoiceIds: List<Uuid>,
    ) {
        companion object {
            @OptIn(ExperimentalUuidApi::class)
            fun fromInvoices(invoices: List<Invoice>): GenerateInvoicesResponse =
                GenerateInvoicesResponse(
                    invoiceIds = invoices.map { Uuid.parse(it.id.toString()) },
                )
        }
    }
