package com.example.org.simpleinvoice.repository

import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.simpleinvoice.model.Household
import org.simpleinvoice.model.Person
import java.util.UUID

interface PersonRepositoryInterface {
    suspend fun all(): List<Person>

    suspend fun add(person: Person): Unit

    suspend fun update(person: Person): Unit

    suspend fun upsert(
        person: Person,
        household: Household,
    ): UpsertStatement<Long>

    fun upsertWithoutTransaction(
        person: Person,
        household: Household,
    ): UpsertStatement<Long>

    suspend fun delete(id: UUID): Boolean
}
