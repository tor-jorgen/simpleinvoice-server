package org.simpleinvoice.server.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.simpleinvoice.server.model.Config
import org.simpleinvoice.server.repository.model.ConfigDAO
import org.simpleinvoice.server.repository.model.ConfigTable
import org.simpleinvoice.server.repository.model.UserTable
import java.util.UUID

class ConfigRepository : ConfigRepositoryInterface {
    override suspend fun all(): List<Config> =
        suspendTransaction {
            ConfigDAO.all().map { it.toConfig() }
        }

    override suspend fun add(config: Config): Unit =
        suspendTransaction {
            ConfigDAO.new {
                defaultDueDays = config.defaultDueDays
                lastInvoiceNumber = config.lastInvoiceNumber
                defaultCurrency = config.defaultCurrency.name
            }
        }

    override suspend fun update(config: Config): Unit =
        suspendTransaction {
            ConfigDAO.findByIdAndUpdate(id = config.id) {
                it.defaultDueDays = config.defaultDueDays
                it.lastInvoiceNumber = config.lastInvoiceNumber
                it.defaultCurrency = config.defaultCurrency.name
            }
        }

    override suspend fun delete(id: UUID): Boolean =
        suspendTransaction {
            val rowsDeleted =
                ConfigTable.deleteWhere {
                    UserTable.id eq id
                }
            rowsDeleted == 1
        }
}
