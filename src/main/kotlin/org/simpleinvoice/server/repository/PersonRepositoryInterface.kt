package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.model.Person
import java.util.UUID

interface PersonRepositoryInterface {
    suspend fun all(): List<Person>

    suspend fun add(person: Person): Unit

    suspend fun update(person: Person): Unit

    fun upsertWithoutTransaction(
        person: Person,
        household: Household,
    ): Person

    suspend fun delete(id: UUID): Boolean
}
