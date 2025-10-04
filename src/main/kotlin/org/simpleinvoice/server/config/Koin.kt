package org.simpleinvoice.server.config

import io.ktor.server.application.Application
import io.ktor.server.application.install
import kotlinx.coroutines.channels.Channel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.odt2pdf.PDFConverter
import org.simpleinvoice.repository.PersonRepository
import org.simpleinvoice.server.invoice.DocumentGenerator
import org.simpleinvoice.server.invoice.EmailGenerator
import org.simpleinvoice.server.invoice.EventPublisher
import org.simpleinvoice.server.invoice.HouseholdImporter
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
import org.simpleinvoice.server.util.smtp.SmtpClient
import org.simpleinvoice.server.util.smtp.SmtpConfig

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
                        applyUnrecognizedMigrationFileFormatFix = property("db.applyUnrecognizedMigrationFileFormatFix").toBoolean(),
                    )
                }

                single {
                    InvoiceConfig(
                        invoiceDirectory = property("invoice.invoiceDirectory"),
                        template = property("invoice.template"),
                        invoiceName = property("invoice.invoiceName"),
                    )
                }

                single {
                    SmtpConfig(
                        host = property("smtp.host"),
                        port = property("smtp.port").toInt(),
                        tls = property("smtp.tls").toBoolean(),
                        username = property("smtp.username"),
                        password = property("smtp.password"),
                        senderEmail = property("smtp.senderEmail"),
                        senderName = property("smtp.senderName"),
                    )
                }

                single {
                    SecurityConfig(
                        clientId = property("security.clientId"),
                        clientSecret = property("security.clientSecret"),
                        allowHosts = property("security.allowHosts").split(",", ";", " ", "\t").map { it.trim() },
                        csrfToken = property("security.csrfToken"),
                    )
                }

                single {
                    PDFConverter()
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
