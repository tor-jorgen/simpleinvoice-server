package org.simpleinvoice.server.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.upsert
import org.simpleinvoice.repository.PersonRepository
import org.simpleinvoice.server.invoice.EventPublisher
import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.model.Person
import org.simpleinvoice.server.repository.model.HouseholdDAO
import org.simpleinvoice.server.repository.model.HouseholdTable
import org.simpleinvoice.server.repository.model.PersonTable
import org.simpleinvoice.server.resources.model.HouseholdsResponse
import java.util.UUID

class HouseholdRepository(
    private val personRepository: PersonRepository,
    private val eventPublisher: EventPublisher,
) : HouseholdRepositoryInterface {
    override suspend fun all(
        activeOnly: Boolean,
        ids: List<UUID>,
    ): HouseholdsResponse =
        suspendTransaction {
            HouseholdsResponse(
                households =
                    if (activeOnly) {
                        HouseholdDAO.find { HouseholdTable.inactive eq false }.map { it.toHousehold() }
                    } else if (ids.isNotEmpty()) {
                        HouseholdDAO.find { HouseholdTable.id inList ids }.map { it.toHousehold() }
                    } else {
                        HouseholdDAO.all().map { it.toHousehold() }
                    },
            )
        }

    override suspend fun get(id: UUID): Household = suspendTransaction { HouseholdDAO[id].toHousehold() }

    override suspend fun upsert(
        household: Household,
        new: Boolean,
    ): Household {
        val persons = mutableListOf<Person>()
        val response =
            suspendTransaction {
                // Delete all persons for the household, since we don't know if any have been removed
                PersonTable.deleteWhere { householdId eq household.id }
                val upsert =
                    HouseholdTable.upsert {
                        it[id] = household.id
                        it[name] = household.description()
                        it[address] = household.address
                        it[address2] = household.address2
                        it[zipCode] = household.zipCode
                        it[city] = household.city
                        it[country] = household.country
                        it[inactive] = household.inactive
                    }
                household.persons.forEach { person ->
                    persons.add(personRepository.upsertWithoutTransaction(person = person, household = household))
                }
                upsert
            }
        eventPublisher.publishEvent(
            id = household.id,
            item = household,
            message = if (new) "Household created" else "Household updated",
        )
        return toHousehold(statement = response, persons = persons)
    }

    override suspend fun delete(id: UUID): Boolean {
        val response =
            suspendTransaction {
                PersonTable.deleteWhere { householdId eq id }
                val rowsDeleted = HouseholdTable.deleteWhere { HouseholdTable.id eq id }
                rowsDeleted == 1
            }
        eventPublisher.publishIdEvent(id = id, message = "Household deleted")
        return response
    }

    private fun toHousehold(
        statement: UpsertStatement<Long>,
        persons: List<Person>,
    ): Household =
        Household(
            id = statement[HouseholdTable.id].value,
            name = statement[HouseholdTable.name],
            address = statement[HouseholdTable.address],
            address2 = statement[HouseholdTable.address2],
            zipCode = statement[HouseholdTable.zipCode],
            city = statement[HouseholdTable.city],
            country = statement[HouseholdTable.country],
            persons = persons,
            inactive = statement[HouseholdTable.inactive],
        )
}
