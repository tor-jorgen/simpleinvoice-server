package org.simpleinvoice.server.config

import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.simpleinvoice.repository.PersonRepository
import org.simpleinvoice.server.invoice.DocumentGenerator
import org.simpleinvoice.server.invoice.EmailGenerator
import org.simpleinvoice.server.invoice.InvoiceBatchConfig
import org.simpleinvoice.server.invoice.InvoiceConfig
import org.simpleinvoice.server.invoice.InvoiceGenerator
import org.simpleinvoice.server.repository.HouseholdRepository
import org.simpleinvoice.server.repository.InvoiceLineRepository
import org.simpleinvoice.server.repository.InvoiceRepository
import org.simpleinvoice.server.repository.ProductRepository
import org.simpleinvoice.server.repository.SettingsRepository
import org.simpleinvoice.server.repository.UserRepository

fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single { InvoiceConfig.fromYaml(System.getenv("INVOICE_CONFIG")) }
                single { InvoiceBatchConfig.fromYaml(System.getenv("BATCH_CONFIG")) }

                singleOf(::SettingsRepository)
                singleOf(::UserRepository)
                singleOf(::HouseholdRepository)
                singleOf(::PersonRepository)
                singleOf(::ProductRepository)
                singleOf(::InvoiceRepository)
                singleOf(::InvoiceLineRepository)
                singleOf(::InvoiceGenerator)
                singleOf(::DocumentGenerator)
                singleOf(::EmailGenerator)
            },
        )
    }
}
