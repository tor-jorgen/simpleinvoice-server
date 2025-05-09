package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class ImportHouseholdsResponse
    @OptIn(ExperimentalUuidApi::class)
    constructor(
        // Uses Uuid to be able to serialize a list of UUIDs
        @SerialName("household_ids") val householdIds: List<Uuid>,
    ) {
        companion object {
            @OptIn(ExperimentalUuidApi::class)
            fun fromUUIDs(ids: List<UUID>): ImportHouseholdsResponse =
                ImportHouseholdsResponse(
                    householdIds = ids.map { Uuid.parse(it.toString()) },
                )
        }
    }
