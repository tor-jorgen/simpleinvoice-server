package org.simpleinvoice.server.config

import io.ktor.server.application.*
import kotlinx.coroutines.channels.Channel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.odt2pdf.PDFConverter
import org.simpleinvoice.repository.PersonRepository
import org.simpleinvoice.server.invoice.*
import org.simpleinvoice.server.model.AuditTrail
import org.simpleinvoice.server.repository.*
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
