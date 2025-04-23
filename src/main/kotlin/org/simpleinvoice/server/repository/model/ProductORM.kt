package org.simpleinvoice.server.repository.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Product
import java.util.UUID

object ProductTable : UUIDTable("product") {
    val productCode = varchar("product_code", 255)
    val productName = varchar("product_name", 255)
    val quantity = integer("quantity")
    val price = double("price")
    val currency = varchar("currency", 255)
    val inactive = bool("inactive")
}

class ProductDAO(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ProductDAO>(ProductTable)

    var productCode by ProductTable.productCode
    var productName by ProductTable.productName
    var quantity by ProductTable.quantity
    var price by ProductTable.price
    var currency by ProductTable.currency
    var inactive by ProductTable.inactive

    fun toProduct(): Product =
        Product(
            id = id.value,
            code = productCode,
            name = productName,
            quantity = quantity,
            price = price,
            currency = Currency.valueOf(currency),
            inactive = inactive,
        )
}
