package org.simpleinvoice.server.repository.model

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Product
import java.util.UUID

object ProductTable : UUIDTable("product") {
    val productCode = varchar("product_code", 255)
    val productName = varchar("product_name", 255)
    val quantity = integer("quantity")
    val price = double("price")
    val currency = varchar("currency", 255)
    val taxPercentage = double("tax_percentage")
    val tax = double("tax")
    val totalPrice = double("total_price")
    val inactive = bool("inactive")
}

object ProductTagsTable : Table("product_tags") {
    val productId = reference("product_id", ProductTable)
    val tagId = reference("tag_id", TagTable)
    override val primaryKey = PrimaryKey(productId, tagId)
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
    var taxPercentage by ProductTable.taxPercentage
    var tax by ProductTable.tax
    var totalPrice by ProductTable.totalPrice
    val tags by TagDAO via ProductTagsTable
    var inactive by ProductTable.inactive

    fun toProduct(): Product =
        Product(
            id = id.value,
            code = productCode,
            name = productName,
            quantity = quantity,
            price = price,
            currency = Currency.valueOf(currency),
            taxPercentage = taxPercentage,
            tax = tax,
            totalPrice = totalPrice,
            tags = tags.map { it.toTag() },
            inactive = inactive,
        )
}
