package org.simpleinvoice.server.repository

import org.simpleinvoice.server.invoice.EventPublisher
import org.simpleinvoice.server.model.Settings
import org.simpleinvoice.server.repository.model.SettingsDAO

class SettingsRepository(
    val eventPublisher: EventPublisher,
) : SettingsRepositoryInterface {
    override suspend fun get(): Settings =
        suspendTransaction {
            getWithoutTransaction()
        }

    override fun getWithoutTransaction(): Settings = SettingsDAO.all().map { it.toSettings() }[0]

    override suspend fun update(settings: Settings) {
        suspendTransaction {
            SettingsDAO.findByIdAndUpdate(id = settings.id) {
                it.defaultDueDays = settings.defaultDueDays
                it.lastInvoiceNumber = settings.lastInvoiceNumber
                it.defaultCurrency = settings.defaultCurrency.name
            }
        }
        eventPublisher.publishEvent(
            id = settings.id,
            item = settings,
            message = "Settings updated",
        )
    }
}
