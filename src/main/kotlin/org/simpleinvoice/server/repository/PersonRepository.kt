package org.simpleinvoice.server.repository

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.statements.UpsertStatement
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.upsert
import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.model.Person
import org.simpleinvoice.server.repository.model.PersonDAO
import org.simpleinvoice.server.repository.model.PersonTable
import java.util.UUID

class PersonRepository : PersonRepositoryInterface {
    override suspend fun all(): List<Person> =
        executeInTransaction {
            PersonDAO.all().map { it.toPerson() }
        }

    override suspend fun add(person: Person): Unit =
        executeInTransaction {
            PersonDAO.new {
                firstName = person.firstName
                lastName = person.lastName
                emailAddress = person.emailAddress
                phoneNumber = person.phoneNumber
            }
        }

    override suspend fun update(person: Person): Unit =
        executeInTransaction {
            PersonDAO.findByIdAndUpdate(id = person.id) {
                it.firstName = person.firstName
                it.lastName = person.lastName
                it.emailAddress = person.emailAddress
                it.phoneNumber = person.phoneNumber
            }
        }

    override fun upsertWithoutTransaction(
        person: Person,
        household: Household,
    ): Person =
        toPerson(
            PersonTable.upsert {
                it[id] = person.id
                it[householdId] = household.id
                it[firstName] = person.firstName
                it[lastName] = person.lastName
                it[emailAddress] = person.emailAddress
                it[phoneNumber] = person.phoneNumber
            },
        )

    override suspend fun delete(id: UUID): Boolean =
        executeInTransaction {
            val rowsDeleted =
                PersonTable.deleteWhere {
                    PersonTable.id eq id
                }
            rowsDeleted == 1
        }

    private fun toPerson(result: UpsertStatement<Long>): Person =
        Person(
            id = result[PersonTable.id].value,
            firstName = result[PersonTable.firstName],
            lastName = result[PersonTable.lastName],
            emailAddress = result[PersonTable.emailAddress],
            phoneNumber = result[PersonTable.phoneNumber],
        )
}
