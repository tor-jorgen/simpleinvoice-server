package com.example.org.simpleinvoice.repository.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.simpleinvoice.model.Invoice
import org.simpleinvoice.repository.model.CustomerDAO
import org.simpleinvoice.repository.model.CustomerTable
import org.simpleinvoice.repository.model.InvoiceLineDAO
import org.simpleinvoice.repository.model.InvoiceLineTable
import java.util.UUID

object InvoiceTable : UUIDTable("invoice") {
    val invoiceNumber = integer("invoice_number")
    val generatedDate = varchar("generated_date", 50)
    val dueDate = varchar("due_date", 50)
    val customer = reference("customer_id", CustomerTable)
}

class InvoiceDAO(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<InvoiceDAO>(InvoiceTable)

    var invoiceNumber by InvoiceTable.invoiceNumber
    var generatedDate by InvoiceTable.generatedDate
    var dueDate by InvoiceTable.dueDate
    var customer by CustomerDAO referencedOn InvoiceTable.customer
    val invoiceLines by InvoiceLineDAO referrersOn InvoiceLineTable.invoice

    fun toInvoice(): Invoice =
        Invoice(
            id = id.value,
            number = invoiceNumber,
            generated = generatedDate,
            dueDate = dueDate,
            customer = customer.toCustomer(),
            invoicelines = invoiceLines.map { it.toInvoiceLine() },
        )
}
