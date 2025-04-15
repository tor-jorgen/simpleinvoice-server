package org.simpleinvoice.repository.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.simpleinvoice.model.Product
import java.util.UUID

object ProductTable : UUIDTable("product") {
    val productCode = varchar("product_code", 255)
    val productName = varchar("product_name", 255)
    val quantity = integer("quantity")
    val price = double("price")
    val currency = varchar("currency", 255)
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

    fun toProduct(): Product =
        Product(
            id = id.value,
            code = productCode,
            name = productName,
            quantity = quantity,
            price = price,
            currency = currency,
        )
}
