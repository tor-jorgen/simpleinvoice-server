package com.example.org.simpleinvoice.repository.model

import com.example.org.simpleinvoice.model.InvoiceStatus
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.simpleinvoice.model.Currency
import org.simpleinvoice.model.Invoice
import org.simpleinvoice.repository.model.InvoiceLineDAO
import org.simpleinvoice.repository.model.InvoiceLineTable
import java.time.Instant
import java.util.UUID

object InvoiceTable : UUIDTable("invoice") {
    val invoiceNumber = integer("invoice_number")
    val status = varchar("status", 50)
    val generatedDate = varchar("generated_date", 50)
    val dueDate = varchar("due_date", 50)
    val finalizedDate = varchar("finalized_date", 50)
    val household = reference("household_id", HouseholdTable)
    val totalPrice = double("total_price")
    val currency = varchar("currency", 255)
}

class InvoiceDAO(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<InvoiceDAO>(InvoiceTable)

    var invoiceNumber by InvoiceTable.invoiceNumber
    var status by InvoiceTable.status
    var generatedDate by InvoiceTable.generatedDate
    var dueDate by InvoiceTable.dueDate
    var finalizedDate by InvoiceTable.finalizedDate
    var household by HouseholdDAO referencedOn InvoiceTable.household
    val invoiceLines by InvoiceLineDAO referrersOn InvoiceLineTable.invoice
    val totalPrice by InvoiceTable.totalPrice
    val currency by InvoiceTable.currency

    fun toInvoice(): Invoice =
        Invoice(
            id = id.value,
            number = invoiceNumber,
            status = InvoiceStatus.valueOf(status),
            generatedDate = Instant.parse(generatedDate),
            dueDate = Instant.parse(dueDate),
            finalizedDate = Instant.parse(finalizedDate),
            household = household.toHousehold(),
            invoicelines = invoiceLines.map { it.toInvoiceLine() },
            totalPrice = totalPrice,
            currency = Currency.valueOf(currency),
        )
}
