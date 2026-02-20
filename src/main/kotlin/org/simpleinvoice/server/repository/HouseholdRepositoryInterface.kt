package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.Household
import java.util.UUID

interface HouseholdRepositoryInterface {
    suspend fun all(
        activeOnly: Boolean,
        ids: List<UUID>,
    ): List<Household>

    suspend fun get(id: UUID): Household

    suspend fun upsert(
        household: Household,
        new: Boolean,
    ): Household

    /**
     * Delete a household with id [id]. All persons in the household will also be deleted
     */
    suspend fun delete(id: UUID): Boolean
}
