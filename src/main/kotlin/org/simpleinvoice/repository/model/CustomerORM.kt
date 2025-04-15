package org.simpleinvoice.repository.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.simpleinvoice.model.Customer
import java.util.UUID

object CustomerTable : UUIDTable("customer") {
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val emailAddress = varchar("email_address", 255)
    val address = varchar("address", 255)
    val zipCode = varchar("zip_code", 50)
    val city = varchar("city", 255)
    val phoneNumber = varchar("phone_number", 255).nullable()
}

class CustomerDAO(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CustomerDAO>(CustomerTable)

    var firstName by CustomerTable.firstName
    var lastName by CustomerTable.lastName
    var emailAddress by CustomerTable.emailAddress
    var address by CustomerTable.address
    var zipCode by CustomerTable.zipCode
    var city by CustomerTable.city
    var phoneNumber by CustomerTable.phoneNumber

    fun toCustomer(): Customer =
        Customer(
            id = id.value,
            firstName = firstName,
            lastName = lastName,
            emailAddress = emailAddress,
            address = address,
            zipCode = zipCode,
            city = city,
            phoneNumber = phoneNumber,
        )
}
