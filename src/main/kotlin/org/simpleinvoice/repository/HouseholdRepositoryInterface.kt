package com.example.org.simpleinvoice.repository

import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.simpleinvoice.model.Household
import java.util.UUID

interface HouseholdRepositoryInterface {
    suspend fun all(): List<Household>

//    suspend fun create(household: Household): Unit

//    suspend fun update(household: Household): Unit

    suspend fun upsert(household: Household): UpsertStatement<Long>

    /**
     * Delete a household with id [id]. All persons in the household will also be deleted
     */
    suspend fun delete(id: UUID): Boolean
}
