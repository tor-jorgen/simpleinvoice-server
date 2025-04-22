package org.simpleinvoice.server.repository.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.simpleinvoice.server.model.Person
import java.util.UUID

object PersonTable : UUIDTable("person") {
    val householdId = reference<UUID>(name = "household_id", foreign = HouseholdTable)
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val emailAddress = varchar("email_address", 255).nullable()
    val phoneNumber = varchar("phone_number", 255).nullable()
}

class PersonDAO(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PersonDAO>(PersonTable)

    var householdId by PersonTable.householdId
    var firstName by PersonTable.firstName
    var lastName by PersonTable.lastName
    var emailAddress by PersonTable.emailAddress
    var phoneNumber by PersonTable.phoneNumber

    fun toPerson(): Person =
        Person(
            id = id.value,
            firstName = firstName,
            lastName = lastName,
            emailAddress = emailAddress,
            phoneNumber = phoneNumber,
        )
}
