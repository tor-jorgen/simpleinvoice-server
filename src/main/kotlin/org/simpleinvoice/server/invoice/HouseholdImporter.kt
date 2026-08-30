package org.simpleinvoice.server.invoice

import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.model.Person
import org.simpleinvoice.server.model.Tag
import org.simpleinvoice.server.repository.HouseholdRepository
import org.simpleinvoice.server.resources.model.ImportHouseholdsRequest
import java.util.UUID

class HouseholdImporter(
    private val householdRepository: HouseholdRepository,
) {
    suspend fun importHouseholds(importHouseholds: ImportHouseholdsRequest): List<UUID> {
        val households = mutableSetOf<Household>()
        var lineNo = 0
        importHouseholds.households.split("\n").forEach { line ->
            if (lineNo++ >= importHouseholds.linesToSkip && line.isNotBlank()) {
                line.split(";").let { columns ->
                    if (columns.size < 10) {
                        throw Exception("Invalid line: $line")
                    }
                    val newPerson = person(columns = columns)
                    val newHousehold =
                        household(
                            columns = columns,
                            person = newPerson,
                            tags = importHouseholds.tags.map { it.toTag() },
                        )
                    val household = households.find { it.equalsIgnoreIdPersonsAndTags(newHousehold) }
                    if (household != null) {
                        households.remove(household)
                        households.add(household.copy(persons = household.persons + newPerson))
                    } else {
                        households.add(newHousehold)
                    }
                }
            }
        }
        households.forEach { householdRepository.upsert(household = it, new = true, message = null) }
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

    private fun household(
        columns: List<String>,
        person: Person,
        tags: List<Tag>,
    ): Household =
        Household(
            id = UUID.randomUUID(),
            name = columns[0].trim(),
            address = columns[1].trim(),
            address2 = columns[2].trim(),
            zipCode = columns[3].trim(),
            city = columns[4].trim(),
            country = columns[5].trim(),
            persons = listOf(person),
            tags = tags,
        )
}
