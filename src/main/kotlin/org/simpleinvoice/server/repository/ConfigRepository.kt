package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.Config
import org.simpleinvoice.server.repository.model.ConfigDAO

class ConfigRepository : ConfigRepositoryInterface {
    override suspend fun get(): Config =
        suspendTransaction {
            ConfigDAO.all().map { it.toConfig() }[0]
        }

    override suspend fun update(config: Config): Unit =
        suspendTransaction {
            ConfigDAO.findByIdAndUpdate(id = config.id) {
                it.defaultDueDays = config.defaultDueDays
                it.lastInvoiceNumber = config.lastInvoiceNumber
                it.defaultCurrency = config.defaultCurrency.name
            }
        }
}
