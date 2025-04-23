package org.simpleinvoice.server.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.upsert
import org.simpleinvoice.repository.PersonRepository
import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.repository.model.HouseholdDAO
import org.simpleinvoice.server.repository.model.HouseholdTable
import org.simpleinvoice.server.repository.model.PersonTable
import org.simpleinvoice.server.resources.model.HouseholdsResponse
import java.util.UUID

class HouseholdRepository(
    private val personRepository: PersonRepository,
) : HouseholdRepositoryInterface {
    override suspend fun all(activeOnly: Boolean): HouseholdsResponse =
        suspendTransaction {
            HouseholdsResponse(
                households =
                    if (activeOnly) {
                        HouseholdDAO.find { HouseholdTable.inactive eq false }.map { it.toHousehold() }
                    } else {
                        HouseholdDAO.all().map { it.toHousehold() }
                    },
            )
        }

    override suspend fun upsert(household: Household): UpsertStatement<Long> =
        suspendTransaction {
            // Delete all persons for the household, since we don't know if any have been removed
            PersonTable.deleteWhere { householdId eq household.id }
            val upsert =
                HouseholdTable.upsert {
                    it[id] = household.id
                    it[name] = household.name
                    it[address] = household.address
                    it[zipCode] = household.zipCode
                    it[city] = household.city
                    it[country] = household.country
                    it[inactive] = household.inactive
                }
            household.persons.forEach { person ->
                personRepository.upsertWithoutTransaction(person = person, household = household)
            }
            upsert
        }

    override suspend fun delete(id: UUID): Boolean =
        suspendTransaction {
            PersonTable.deleteWhere { householdId eq id }
            val rowsDeleted = HouseholdTable.deleteWhere { HouseholdTable.id eq id }
            rowsDeleted == 1
        }
}
