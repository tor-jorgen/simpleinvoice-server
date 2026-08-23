package org.simpleinvoice.server.model

import org.simpleinvoice.server.model.InvoiceStatus.CANCELLED
import org.simpleinvoice.server.model.InvoiceStatus.CREATED
import org.simpleinvoice.server.model.InvoiceStatus.DELIVERED
import org.simpleinvoice.server.model.InvoiceStatus.PAID
import org.simpleinvoice.server.model.InvoiceStatus.REPLACED
import java.time.Instant
import java.util.UUID

private val validStatusTransitions: Map<InvoiceStatus, Set<InvoiceStatus>> =
    mapOf(
        CREATED to setOf(CREATED, DELIVERED, CANCELLED),
        DELIVERED to setOf(DELIVERED, CANCELLED, PAID),
        PAID to setOf(PAID),
        REPLACED to setOf(REPLACED),
        CANCELLED to setOf(CANCELLED),
    )

/**
 * This is not a dataclass because the copy function has validation logic.
 */
class Invoice(
    val id: UUID,
    val invoiceNumber: Int,
    val status: InvoiceStatus,
    val generatedDate: Instant,
    val dueDate: Instant,
    val finalizedDate: Instant?,
    val price: Double,
    val tax: Double,
    val totalPrice: Double,
    val currency: Currency,
    val household: Household,
    val invoiceFilePath: String?,
    val invoiceLines: List<InvoiceLine>,
    val tags: List<Tag>,
) {
    fun generatedDateAsString(): String = generatedDate.toString().substring(0, 10)

    fun dueDateAsString(): String = dueDate.toString().substring(0, 10)

    fun finalizedDateAsString(): String = finalizedDate?.toString()?.substring(0, 10) ?: ""

    // @formatter:off
    override fun equals(other: Any?): Boolean { // NOSONAR // equals should support all fields
        // @formatter:on
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Invoice

        if (invoiceNumber != other.invoiceNumber) return false
        if (price != other.price) return false
        if (tax != other.tax) return false
        if (totalPrice != other.totalPrice) return false
        if (id != other.id) return false
        if (generatedDate != other.generatedDate) return false
        if (dueDate != other.dueDate) return false
        if (finalizedDate != other.finalizedDate) return false
        if (currency != other.currency) return false
        if (household != other.household) return false
        if (invoiceFilePath != other.invoiceFilePath) return false
        if (invoiceLines != other.invoiceLines) return false
        if (tags != other.tags) return false
        if (status != other.status) return false

        return true
    }

    override fun hashCode(): Int {
        var result = invoiceNumber
        result = 31 * result + price.hashCode()
        result = 31 * result + tax.hashCode()
        result = 31 * result + totalPrice.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + generatedDate.hashCode()
        result = 31 * result + dueDate.hashCode()
        result = 31 * result + finalizedDate.hashCode()
        result = 31 * result + currency.hashCode()
        result = 31 * result + household.hashCode()
        result = 31 * result + invoiceFilePath.hashCode()
        result = 31 * result + invoiceLines.hashCode()
        result = 31 * result + tags.hashCode()
        result = 31 * result + status.hashCode()
        return result
    }

    // @formatter:off
    fun copy( // NOSONAR // copy should support all fields
        // @formatter:on
        id: UUID = this.id,
        invoiceNumber: Int = this.invoiceNumber,
        status: InvoiceStatus = this.status,
        generatedDate: Instant = this.generatedDate,
        dueDate: Instant = this.dueDate,
        finalizedDate: Instant? = this.finalizedDate,
        price: Double = this.price,
        tax: Double = this.tax,
        totalPrice: Double = this.totalPrice,
        currency: Currency = this.currency,
        household: Household = this.household,
        invoiceFilePath: String? = this.invoiceFilePath,
        invoiceLines: List<InvoiceLine> = this.invoiceLines,
        tags: List<Tag> = this.tags,
    ): Invoice {
        validateStatus(status)

        return Invoice(
            id = id,
            invoiceNumber = invoiceNumber,
            status = status,
            generatedDate = generatedDate,
            dueDate = dueDate,
            finalizedDate = finalizedDate,
            price = price,
            tax = tax,
            totalPrice = totalPrice,
            currency = currency,
            household = household,
            invoiceFilePath = invoiceFilePath,
            invoiceLines = invoiceLines,
            tags = tags,
        )
    }

    private fun validateStatus(status: InvoiceStatus) {
        check(validStatusTransitions.containsKey(status)) { "Cannot find status: $status" }
        check(
            validStatusTransitions[this.status]?.contains(status) == true,
        ) { "Status cannot be changed from ${this.status} to $status" }
    }

    override fun toString(): String =
        "Invoice(id=$id, invoiceNumber=$invoiceNumber, generatedDate=$generatedDate, dueDate=$dueDate, finalizedDate=$finalizedDate, price=$price, tax=$tax, totalPrice=$totalPrice, currency=$currency, household=$household, invoiceFilePath=$invoiceFilePath, invoiceLines=$invoiceLines, tags=$tags, status=$status)"
}
