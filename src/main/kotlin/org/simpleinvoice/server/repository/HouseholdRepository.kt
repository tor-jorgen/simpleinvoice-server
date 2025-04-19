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
import java.util.UUID

class HouseholdRepository(private val personRepository: PersonRepository) :
    HouseholdRepositoryInterface {

    override suspend fun all(): List<Household> =
        suspendTransaction {
            HouseholdDAO.all().map { it.toHousehold() }
        }

    override suspend fun upsert(household: Household): UpsertStatement<Long> =
        suspendTransaction {
            val upsert =
                HouseholdTable.upsert {
                    it[id] = household.id
                    it[address] = household.address
                    it[zipCode] = household.zipCode
                    it[city] = household.city
                    it[country] = household.country
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
