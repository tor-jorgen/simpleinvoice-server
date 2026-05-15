package org.simpleinvoice.server.repository.model

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Settings
import java.util.UUID

object SettingsTable : UUIDTable("settings") {
    val defaultDueDays = integer("default_due_days")
    val lastInvoiceNumber = integer("last_invoice_number")
    val defaultTaxPercentage = double("default_tax_percentage")
    val defaultCurrency = varchar("default_currency", 5)
    val defaultEmailSubject = varchar("default_email_subject", 1024).nullable()
    val defaultEmailText = varchar("default_email_text", 8192).nullable()
}

class SettingsDAO(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SettingsDAO>(SettingsTable)

    var defaultDueDays by SettingsTable.defaultDueDays
    var lastInvoiceNumber by SettingsTable.lastInvoiceNumber
    var defaultTaxPercentage by SettingsTable.defaultTaxPercentage
    var defaultCurrency by SettingsTable.defaultCurrency
    var defaultEmailSubject by SettingsTable.defaultEmailSubject
    var defaultEmailText by SettingsTable.defaultEmailText

    fun toSettings(): Settings =
        Settings(
            id = id.value,
            defaultDueDays = defaultDueDays,
            lastInvoiceNumber = lastInvoiceNumber,
            defaultTaxPercentage = defaultTaxPercentage,
            defaultCurrency = Currency.valueOf(defaultCurrency),
            defaultEmailSubject = defaultEmailSubject,
            defaultEmailText = defaultEmailText,
        )
}
