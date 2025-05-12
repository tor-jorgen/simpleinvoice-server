package org.simpleinvoice.server.repository.model

import org.jetbrains.exposed.sql.Table

object HouseholdTagsTable : Table("household_tags") {
    val householdId = reference("household_id", HouseholdTable)
    val tagId = reference("tag_id", TagTable)
    override val primaryKey = PrimaryKey(householdId, tagId)
}

// class HouseholdTagsDAO(
//    id: EntityID<UUID>,
// ) : UUIDEntity(id) {
//    companion object : UUIDEntityClass<HouseholdDAO>(HouseholdTable)
//
//    var household by HouseholdTable.household
//    var address by HouseholdTable.address
//
//    fun toHousehold(): Household =
//        Household(
//            id = id.value,
//            name = name,
//            address = address,
//            address2 = address2,
//            zipCode = zipCode,
//            city = city,
//            country = country,
//            persons = persons.map { Entities.it.toPerson() },
//            inactive = inactive,
//        )
// }
