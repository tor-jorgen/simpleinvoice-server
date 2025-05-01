package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.InstantSerializer
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.InvoiceStatus
import java.time.Instant
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class GenerateInvoicesRequest
    @OptIn(ExperimentalUuidApi::class)
    constructor(
        val status: InvoiceStatus,
        @Serializable(with = InstantSerializer::class) @SerialName("due_date") val dueDate: Instant,
        @SerialName("total_price") val totalPrice: Double,
        val currency: Currency,
        @SerialName("invoice_lines") val invoiceLines: List<GenerateInvoiceLineRequest>,
        // Uses Uuid to be able to serialize a list of UUIDs
        @SerialName("household_ids") val householdIds: List<Uuid>,
    )

@Serializable
data class GenerateInvoiceLineRequest
    @OptIn(ExperimentalUuidApi::class)
    constructor(
        @SerialName("line_number") val lineNumber: Int,
        @Serializable(with = UUIDSerializer::class) @SerialName("product_id") val productId: UUID,
        val quantity: Int,
        @SerialName("total_price") val totalPrice: Double,
        val currency: Currency,
    )
