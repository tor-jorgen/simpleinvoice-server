package org.simpleinvoice.server.repository.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.InvoiceLine
import java.util.UUID

object InvoiceLineTable : UUIDTable("invoice_line") {
    val invoiceId = reference(name = "invoice_id", foreign = InvoiceTable)
    val lineNumber = integer("line_number")
    val productId = reference("product_id", ProductTable)
    val quantity = integer("quantity")
    val totalPrice = double("total_price")
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
    var totalPrice by InvoiceLineTable.totalPrice
    var currency by InvoiceLineTable.currency

    fun toInvoiceLine(): InvoiceLine =
        InvoiceLine(
            id = id.value,
            lineNumber = lineNumber,
            product = product.toProduct(),
            quantity = quantity,
            totalPrice = totalPrice,
            currency = Currency.valueOf(currency),
        )
}
