package org.simpleinvoice.server.repository.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.simpleinvoice.server.model.Household
import java.util.UUID

object HouseholdTable : UUIDTable("household") {
    val name = varchar("name", 255).nullable()
    val address = varchar("address", 255)
    val address2 = varchar("address2", 255).nullable()
    val zipCode = varchar("zip_code", 50)
    val city = varchar("city", 255)
    val country = varchar("country", 255).nullable()
    val inactive = bool("inactive")
}

class HouseholdDAO(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<HouseholdDAO>(HouseholdTable)

    var name by HouseholdTable.name
    var address by HouseholdTable.address
    var address2 by HouseholdTable.address2
    var zipCode by HouseholdTable.zipCode
    var city by HouseholdTable.city
    var country by HouseholdTable.country
    val persons by PersonDAO referrersOn PersonTable.householdId
    val inactive: Boolean by HouseholdTable.inactive

    fun toHousehold(): Household =
        Household(
            id = id.value,
            name = name,
            address = address,
            address2 = address2,
            zipCode = zipCode,
            city = city,
            country = country,
            persons = persons.map { it.toPerson() },
            inactive = inactive,
        )
}
