package org.simpleinvoice.server.repository.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.simpleinvoice.server.model.Config
import org.simpleinvoice.server.model.Currency
import java.util.UUID

object ConfigTable : UUIDTable("application_config") {
    val defaultDueDays = integer("default_due_days")
    val lastInvoiceNumber = integer("last_invoice_number")
    val defaultCurrency = varchar("default_currency", 5)
}

class ConfigDAO(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ConfigDAO>(ConfigTable)

    var defaultDueDays by ConfigTable.defaultDueDays
    var lastInvoiceNumber by ConfigTable.lastInvoiceNumber
    var defaultCurrency by ConfigTable.defaultCurrency

    fun toConfig(): Config =
        Config(
            id = id.value,
            defaultDueDays = defaultDueDays,
            lastInvoiceNumber = lastInvoiceNumber,
            defaultCurrency = Currency.valueOf(defaultCurrency),
        )
}
