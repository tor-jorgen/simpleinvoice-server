package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID
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
            fun fromUUIDs(ids: List<UUID>): GenerateInvoicesResponse =
                GenerateInvoicesResponse(
                    invoiceIds = ids.map { Uuid.parse(it.toString()) },
                )
        }
    }
