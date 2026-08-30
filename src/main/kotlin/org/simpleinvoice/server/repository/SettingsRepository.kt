package org.simpleinvoice.server.repository

import org.simpleinvoice.server.invoice.EventPublisher
import org.simpleinvoice.server.model.Settings
import org.simpleinvoice.server.repository.model.SettingsDAO

class SettingsRepository(
    val eventPublisher: EventPublisher,
) : SettingsRepositoryInterface {
    override suspend fun get(): Settings =
        executeInTransaction {
            getWithoutTransaction()
        }

    override fun getWithoutTransaction(): Settings = SettingsDAO.all().map { it.toSettings() }[0]

    override suspend fun update(
        settings: Settings,
        message: String?,
    ): Settings {
        val response =
            executeInTransaction {
                SettingsDAO
                    .findByIdAndUpdate(id = settings.id) {
                        it.defaultDueDays = settings.defaultDueDays
                        it.lastInvoiceNumber = settings.lastInvoiceNumber
                        it.defaultTaxPercentage = settings.defaultTaxPercentage
                        it.defaultCurrency = settings.defaultCurrency.name
                        it.defaultEmailSubject = settings.defaultEmailSubject
                        it.defaultEmailText = settings.defaultEmailText
                    }?.toSettings()!!
            }
        eventPublisher.publishEvent(
            id = settings.id,
            item = settings,
            message = "Settings updated",
            userMessage = message,
        )
        return response
    }
}
