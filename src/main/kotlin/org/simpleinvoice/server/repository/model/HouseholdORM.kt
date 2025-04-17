package org.simpleinvoice.server.repository.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.simpleinvoice.server.model.Household
import java.util.UUID

object HouseholdTable : UUIDTable("household") {
    val address = varchar("address", 255)
    val zipCode = varchar("zip_code", 50)
    val city = varchar("city", 255)
    val country = varchar("country", 255).nullable()
}

class HouseholdDAO(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<HouseholdDAO>(HouseholdTable)

    var address by HouseholdTable.address
    var zipCode by HouseholdTable.zipCode
    var city by HouseholdTable.city
    var country by HouseholdTable.country
    val persons by PersonDAO referrersOn PersonTable.householdId

    fun toHousehold(): Household =
        Household(
            id = id.value,
            address = address,
            zipCode = zipCode,
            city = city,
            country = country,
            persons = persons.map { it.toPerson() },
        )
}
