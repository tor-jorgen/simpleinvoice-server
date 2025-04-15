package org.simpleinvoice.repository.model

import com.example.org.simpleinvoice.repository.model.InvoiceTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.simpleinvoice.model.InvoiceLine
import java.util.UUID

object InvoiceLineTable : UUIDTable("invoice_line") {
    val invoice = reference("invoice_id", InvoiceTable)
    val lineNumber = integer("line_number")
    val product = reference("product_id", ProductTable)
    val quantity = integer("quantity")
    val totalPrice = double("total_price")
}

class InvoiceLineDAO(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<InvoiceLineDAO>(InvoiceLineTable)

    var invoice by InvoiceLineTable.invoice
    var lineNumber by InvoiceLineTable.lineNumber
    var product by ProductDAO referencedOn InvoiceLineTable.product
    var quantity by InvoiceLineTable.quantity
    var totalPrice by InvoiceLineTable.totalPrice

    fun toInvoiceLine(): InvoiceLine =
        InvoiceLine(
            id = id.value,
            lineNumber = lineNumber,
            product = product.toProduct(),
            quantity = quantity,
            totalPrice = totalPrice,
        )
}
