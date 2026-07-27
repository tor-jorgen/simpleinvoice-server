package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.model.InvoiceStatus
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class BulkUpdateInvoicesRequest
    @OptIn(ExperimentalUuidApi::class)
    constructor(
        // Uses kotlin.uuid.Uuid to be able to serialize a list of UUIDs
        @SerialName("invoice_ids") val invoiceIds: List<Uuid>,
        val status: InvoiceStatus,
        val message: String? = null,
    )
