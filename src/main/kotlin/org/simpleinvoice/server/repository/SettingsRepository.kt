package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.Settings
import org.simpleinvoice.server.repository.model.SettingsDAO

class SettingsRepository : SettingsRepositoryInterface {
    override suspend fun get(): Settings =
        suspendTransaction {
            getWithoutTransaction()
        }

    override fun getWithoutTransaction(): Settings = SettingsDAO.all().map { it.toSettings() }[0]

    override suspend fun update(settings: Settings): Unit =
        suspendTransaction {
            SettingsDAO.findByIdAndUpdate(id = settings.id) {
                it.defaultDueDays = settings.defaultDueDays
                it.lastInvoiceNumber = settings.lastInvoiceNumber
                it.defaultCurrency = settings.defaultCurrency.name
            }
        }
}
