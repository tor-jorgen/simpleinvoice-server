package com.example.org.simpleinvoice.repository

import com.example.org.simpleinvoice.repository.model.HouseholdDAO
import com.example.org.simpleinvoice.repository.model.HouseholdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.upsert
import org.simpleinvoice.model.Household
import org.simpleinvoice.repository.PersonRepository
import org.simpleinvoice.repository.model.PersonTable
import org.simpleinvoice.repository.suspendTransaction
import java.util.UUID

class HouseholdRepository : HouseholdRepositoryInterface {
    private val personRepository = PersonRepository() // TODO: Inject

    override suspend fun all(): List<Household> =
        suspendTransaction {
            HouseholdDAO.all().map { it.toHousehold() }
        }

//    override suspend fun create(household: Household): Unit =
//        suspendTransaction {
//            HouseholdDAO.new {
//                address = household.address
//                zipCode = household.zipCode
//                city = household.city
//            }
//        }

//    override suspend fun update(household: Household): Unit =
//        suspendTransaction {
//            HouseholdDAO.findByIdAndUpdate(id = household.id) {
//                it.address = household.address
//                it.zipCode = household.zipCode
//                it.city = household.city
//            }
//        }

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

// onUpdate =
// UpdateStatement()
// mutableListOf(
// HouseholdTable.address to stringLiteral(household.address),
// HouseholdTable.zipCode to stringLiteral(household.zipCode),
// HouseholdTable.city to stringLiteral(household.city),
// ),
