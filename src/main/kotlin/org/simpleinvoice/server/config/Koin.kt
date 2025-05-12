package org.simpleinvoice.server.config

import io.ktor.server.application.Application
import io.ktor.server.application.install
import kotlinx.coroutines.channels.Channel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.simpleinvoice.repository.PersonRepository
import org.simpleinvoice.server.invoice.DocumentGenerator
import org.simpleinvoice.server.invoice.EmailGenerator
import org.simpleinvoice.server.invoice.EventPublisher
import org.simpleinvoice.server.invoice.HouseholdImporter
import org.simpleinvoice.server.invoice.InvoiceBatchConfig
import org.simpleinvoice.server.invoice.InvoiceConfig
import org.simpleinvoice.server.invoice.InvoiceGenerator
import org.simpleinvoice.server.model.AuditTrail
import org.simpleinvoice.server.repository.AuditTrailRepository
import org.simpleinvoice.server.repository.HouseholdRepository
import org.simpleinvoice.server.repository.InvoiceLineRepository
import org.simpleinvoice.server.repository.InvoiceRepository
import org.simpleinvoice.server.repository.ProductRepository
import org.simpleinvoice.server.repository.SettingsRepository
import org.simpleinvoice.server.repository.TagRepository
import org.simpleinvoice.server.repository.UserRepository
import util.smtp.SmtpClient

fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        modules(
            module {
                val dbConnectionPrefix = property("db.connectionPrefix")
                val dbPort = property("db.port")
                val dbName = property("db.name")
                single {
                    DatabaseConfig(
                        connectionString = "$dbConnectionPrefix:$dbPort/$dbName",
                        user = property("db.user"),
                        password = property("db.password"),
                    )
                }

                val invoiceConfig = InvoiceConfig.fromYaml(property("cfg.invoice"))
                single { invoiceConfig }
                single { invoiceConfig.smtp }
                single { InvoiceBatchConfig.fromYaml(property("cfg.batch")) }

                single {
                    SecurityConfig(
                        clientId = property("security.clientId"),
                        clientSecret = property("security.clientSecret"),
                    )
                }

                single {
                    Channel<AuditTrail>(capacity = 100)
                }

                singleOf(::SmtpClient)
                singleOf(::EventPublisher)
                singleOf(::SettingsRepository)
                singleOf(::UserRepository)
                singleOf(::HouseholdRepository)
                singleOf(::PersonRepository)
                singleOf(::ProductRepository)
                singleOf(::InvoiceRepository)
                singleOf(::InvoiceLineRepository)
                singleOf(::AuditTrailRepository)
                singleOf(::TagRepository)
                singleOf(::InvoiceGenerator)
                singleOf(::DocumentGenerator)
                singleOf(::EmailGenerator)
                singleOf(::HouseholdImporter)
            },
        )
    }
}

private fun Application.property(name: String): String = environment.config.propertyOrNull(name)?.getString()!!
