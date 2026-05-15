package org.simpleinvoice.server.repository.model

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.InvoiceLine
import java.util.UUID

object InvoiceLineTable : UUIDTable("invoice_line") {
    val invoiceId = reference(name = "invoice_id", foreign = InvoiceTable)
    val lineNumber = integer("line_number")
    val productId = reference("product_id", ProductTable)
    val quantity = integer("quantity")
    val totalPrice = double("total_price")
    val price = double("price")
    val tax = double("tax")
    val currency = varchar("currency", 50)
}

class InvoiceLineDAO(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<InvoiceLineDAO>(InvoiceLineTable)

    var invoiceId by InvoiceLineTable.invoiceId
    var lineNumber by InvoiceLineTable.lineNumber
    var product by ProductDAO referencedOn InvoiceLineTable.productId
    var quantity by InvoiceLineTable.quantity
    var price by InvoiceLineTable.price
    var tax by InvoiceLineTable.tax
    var totalPrice by InvoiceLineTable.totalPrice
    var currency by InvoiceLineTable.currency

    fun toInvoiceLine(): InvoiceLine =
        InvoiceLine(
            id = id.value,
            lineNumber = lineNumber,
            product = product.toProduct(),
            quantity = quantity,
            price = price,
            tax = tax,
            totalPrice = totalPrice,
            currency = Currency.valueOf(currency),
        )
}
