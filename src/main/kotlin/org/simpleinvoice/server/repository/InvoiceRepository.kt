package org.simpleinvoice.server.repository

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.core.statements.UpsertStatement
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.upsert
import org.simpleinvoice.server.invoice.EventPublisher
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceLine
import org.simpleinvoice.server.model.InvoiceStatus
import org.simpleinvoice.server.model.Tag
import org.simpleinvoice.server.repository.model.InvoiceDAO
import org.simpleinvoice.server.repository.model.InvoiceLineTable
import org.simpleinvoice.server.repository.model.InvoiceTable
import org.simpleinvoice.server.repository.model.InvoiceTagsTable
import java.time.Instant
import java.util.UUID

class InvoiceRepository(
    private val invoiceLineRepository: InvoiceLineRepository,
    private val settingsRepository: SettingsRepository,
    private val eventPublisher: EventPublisher,
) : InvoiceRepositoryInterface {
    override suspend fun all(
        activeOnly: Boolean,
        ids: List<UUID>,
    ): List<Invoice> =
        executeInTransaction {
            if (activeOnly) {
                InvoiceDAO
                    .find(InvoiceTable.status eq InvoiceStatus.CREATED.name or (InvoiceTable.status eq InvoiceStatus.DELIVERED.name))
                    .map { it.toInvoice() }
            } else if (ids.isNotEmpty()) {
                InvoiceDAO.find { InvoiceTable.id inList ids }.map { it.toInvoice() }
            } else {
                InvoiceDAO.all().map { it.toInvoice() }
            }
        }

    override suspend fun get(id: UUID): Invoice =
        executeInTransaction {
            getWithoutTransaction(id)
        }

    private fun getWithoutTransaction(id: UUID): Invoice =
        InvoiceDAO.findById(id)?.toInvoice() ?: throw Exception("Invoice with id: $id not found")

    override suspend fun upsert(
        invoice: Invoice,
        new: Boolean,
        message: String?,
    ): Invoice {
        val response =
            executeInTransaction {
                // Delete all invoice lines and tags for the invoice, since we don't know if any have been removed
                InvoiceLineTable.deleteWhere { invoiceId eq invoice.id }
                InvoiceTagsTable.deleteWhere { invoiceId eq invoice.id }
                val dbInvoice =
                    if (new) {
                        // Generate a new invoice number
                        invoice.copy(
                            invoiceNumber =
                                nextInvoiceNumber()
                                    ?: (settingsRepository.getWithoutTransaction().lastInvoiceNumber + 1),
                        )
                    } else {
                        getWithoutTransaction(invoice.id).copy(
                            status = invoice.status,
                            dueDate = invoice.dueDate,
                            finalizedDate = invoice.finalizedDate,
                            price = invoice.price,
                            tax = invoice.tax,
                            totalPrice = invoice.totalPrice,
                            currency = invoice.currency,
                            household = invoice.household,
                            invoiceFilePath = invoice.invoiceFilePath,
                            invoiceLines = invoice.invoiceLines,
                            tags = invoice.tags,
                        )
                    }
                val upsert = upsertWithoutTransaction(dbInvoice)
                invoice.invoiceLines.forEach { invoiceLine ->
                    invoiceLineRepository.upsertWithoutTransaction(invoiceLine, dbInvoice)
                }
                InvoiceTagsTable.batchUpsert(
                    data = invoice.tags,
                    body = { tag: Tag ->
                        this[InvoiceTagsTable.invoiceId] = invoice.id
                        this[InvoiceTagsTable.tagId] = tag.id
                    },
                )
                upsert
            }
        val responseInvoice =
            toInvoice(
                statement = response,
                household = invoice.household,
                invoiceLines = invoice.invoiceLines,
                tags = invoice.tags,
            )
        eventPublisher.publishEvent(
            id = responseInvoice.id,
            item = responseInvoice,
            message = if (new) "Invoice created" else "Invoice updated",
            userMessage = message,
        )
        return responseInvoice
    }

    private fun upsertWithoutTransaction(invoice: Invoice): UpsertStatement<Long> =
        InvoiceTable.upsert(onUpdateExclude = listOf(InvoiceTable.invoiceNumber, InvoiceTable.generatedDate)) {
            it[id] = invoice.id
            it[invoiceNumber] = invoice.invoiceNumber
            it[status] = invoice.status.name
            it[generatedDate] = invoice.generatedDate.toString()
            it[dueDate] = invoice.dueDate.toString()
            it[finalizedDate] = invoice.finalizedDate?.toString() ?: kotlin.run { null }
            it[householdId] = invoice.household.id
            it[price] = invoice.price
            it[tax] = invoice.tax
            it[totalPrice] = invoice.totalPrice
            it[currency] = invoice.currency.name
            it[invoiceFilePath] = invoice.invoiceFilePath
        }

    override suspend fun delete(
        id: UUID,
        message: String?,
    ): Boolean {
        val response =
            executeInTransaction {
                InvoiceLineTable.deleteWhere { invoiceId eq id }
                InvoiceTagsTable.deleteWhere { invoiceId eq id }
                val rowsDeleted = InvoiceTable.deleteWhere { InvoiceTable.id eq id }
                rowsDeleted == 1
            }
        eventPublisher.publishIdEvent(id = id, message = "Invoice deleted")
        return response
    }

    override fun nextInvoiceNumber(): Int? = InvoiceDAO.all().maxOfOrNull { it.invoiceNumber }?.plus(1)

    private fun toInvoice(
        statement: UpsertStatement<Long>,
        household: Household,
        invoiceLines: List<InvoiceLine>,
        tags: List<Tag>,
    ): Invoice =
        Invoice(
            id = statement[InvoiceTable.id].value,
            invoiceNumber = statement[InvoiceTable.invoiceNumber],
            status = InvoiceStatus.valueOf(statement[InvoiceTable.status]),
            generatedDate = Instant.parse(statement[InvoiceTable.generatedDate]),
            dueDate = Instant.parse(statement[InvoiceTable.dueDate]),
            finalizedDate = statement[InvoiceTable.finalizedDate]?.let { Instant.parse(it) },
            invoiceLines = invoiceLines,
            price = statement[InvoiceTable.price],
            tax = statement[InvoiceTable.tax],
            totalPrice = statement[InvoiceTable.totalPrice],
            currency = Currency.valueOf(statement[InvoiceTable.currency]),
            invoiceFilePath = statement[InvoiceTable.invoiceFilePath],
            household = household,
            tags = tags,
        )
}
