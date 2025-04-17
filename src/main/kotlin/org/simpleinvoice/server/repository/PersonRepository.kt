package org.simpleinvoice.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.upsert
import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.model.Person
import org.simpleinvoice.server.repository.PersonRepositoryInterface
import org.simpleinvoice.server.repository.model.PersonDAO
import org.simpleinvoice.server.repository.model.PersonTable
import org.simpleinvoice.server.repository.suspendTransaction
import java.util.UUID

class PersonRepository : PersonRepositoryInterface {
    override suspend fun all(): List<Person> =
        suspendTransaction {
            PersonDAO.all().map { it.toPerson() }
        }

    override suspend fun add(person: Person): Unit =
        suspendTransaction {
            PersonDAO.new {
                firstName = person.firstName
                lastName = person.lastName
                emailAddress = person.emailAddress
                phoneNumber = person.phoneNumber
            }
        }

    override suspend fun update(person: Person): Unit =
        suspendTransaction {
            PersonDAO.findByIdAndUpdate(id = person.id) {
                it.firstName = person.firstName
                it.lastName = person.lastName
                it.emailAddress = person.emailAddress
                it.phoneNumber = person.phoneNumber
            }
        }

    override suspend fun upsert(
        person: Person,
        household: Household,
    ): UpsertStatement<Long> =
        suspendTransaction {
            upsertWithoutTransaction(person = person, household = household)
        }

    override fun upsertWithoutTransaction(
        person: Person,
        household: Household,
    ): UpsertStatement<Long> =
        PersonTable.upsert {
            it[householdId] = household.id
            it[firstName] = person.firstName
            it[lastName] = person.lastName
            it[emailAddress] = person.emailAddress
            it[phoneNumber] = person.phoneNumber
        }

    override suspend fun delete(id: UUID): Boolean =
        suspendTransaction {
            val rowsDeleted =
                PersonTable.deleteWhere {
                    PersonTable.id eq id
                }
            rowsDeleted == 1
        }
}
