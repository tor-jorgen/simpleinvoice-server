package org.simpleinvoice.server.repository.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceStatus
import java.time.Instant
import java.util.UUID

object InvoiceTable : UUIDTable("invoice") {
    val invoiceNumber = integer("invoice_number")
    val status = varchar("status", 10)
    val generatedDate = varchar("generated_date", 30)
    val dueDate = varchar("due_date", 30)
    val finalizedDate = varchar("finalized_date", 30).nullable()
    val householdId = reference(name = "household_id", foreign = HouseholdTable)
    val totalPrice = double("total_price")
    val currency = varchar("currency", 255)
    val invoiceFilePath = varchar("invoice_file_path", 255).nullable()
}

object InvoiceTagsTable : Table("invoice_tags") {
    val invoiceId = reference("invoice_id", InvoiceTable)
    val tagId = reference("tag_id", TagTable)
    override val primaryKey = PrimaryKey(invoiceId, tagId)
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
    var household by HouseholdDAO referencedOn InvoiceTable.householdId
    val invoiceLines by InvoiceLineDAO referrersOn InvoiceLineTable.invoiceId
    val totalPrice by InvoiceTable.totalPrice
    val currency by InvoiceTable.currency
    val invoiceFilePath by InvoiceTable.invoiceFilePath
    val tags by TagDAO via InvoiceTagsTable

    fun toInvoice(): Invoice =
        Invoice(
            id = id.value,
            invoiceNumber = invoiceNumber,
            status = InvoiceStatus.valueOf(status),
            generatedDate = Instant.parse(generatedDate),
            dueDate = Instant.parse(dueDate),
            finalizedDate = finalizedDate?.let { Instant.parse(it) },
            household = household.toHousehold(),
            invoiceLines = invoiceLines.map { it.toInvoiceLine() },
            totalPrice = totalPrice,
            currency = Currency.valueOf(currency),
            invoiceFilePath = invoiceFilePath,
            tags = tags.map { it.toTag() },
        )
}
