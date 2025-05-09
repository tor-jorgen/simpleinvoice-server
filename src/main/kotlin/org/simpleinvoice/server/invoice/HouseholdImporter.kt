package org.simpleinvoice.server.invoice

import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.model.Person
import org.simpleinvoice.server.repository.HouseholdRepository
import java.util.UUID

class HouseholdImporter(
    householdRepository: HouseholdRepository,
) {
    fun importHouseholds(householdString: String): List<UUID> {
        val households = mutableSetOf<Household>()
        var lineNo = 0
        householdString.split("\n").forEach { line ->
            if (lineNo++ > 0 && line.isNotBlank()) {
                line.split(";").let { columns ->
                    if (columns.size < 10) {
                        throw Exception("Invalid line: $line")
                    }
                    val newHousehold = household(columns = columns)
                    val household = households.find { it.equalsIgnoreIdAndPersons(newHousehold) }
                    val newPerson = person(columns)
                    if (household != null) {
                        households.remove(household)
                        households.add(household.copy(persons = household.persons + newPerson))
                    } else {
                        households.add(newHousehold.copy(persons = listOf(newPerson)))
                    }
                }
            }
        }
        return households.map { it.id }
    }

    private fun person(columns: List<String>): Person =
        Person(
            id = UUID.randomUUID(),
            firstName = columns[6].trim(),
            lastName = columns[7].trim(),
            emailAddress = columns[8].trim(),
            phoneNumber = columns[9].trim(),
        )

    private fun household(columns: List<String>): Household =
        Household(
            id = UUID.randomUUID(),
            name = columns[0].trim(),
            address = columns[1].trim(),
            address2 = columns[2].trim(),
            zipCode = columns[3].trim(),
            city = columns[4].trim(),
            country = columns[5].trim(),
            persons = mutableListOf(),
        )
}
