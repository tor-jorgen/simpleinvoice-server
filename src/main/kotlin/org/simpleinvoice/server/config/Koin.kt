package org.simpleinvoice.server.config

import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.simpleinvoice.repository.PersonRepository
import org.simpleinvoice.server.repository.HouseholdRepository
import org.simpleinvoice.server.repository.InvoiceLineRepository
import org.simpleinvoice.server.repository.InvoiceRepository
import org.simpleinvoice.server.repository.ProductRepository
import org.simpleinvoice.server.repository.SettingsRepository
import org.simpleinvoice.server.repository.UserRepository
import org.simpleinvoice.server.resources.InvoiceGenerator

fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        modules(
            module {
                singleOf(::SettingsRepository)
                singleOf(::UserRepository)
                singleOf(::HouseholdRepository)
                singleOf(::PersonRepository)
                singleOf(::ProductRepository)
                singleOf(::InvoiceRepository)
                singleOf(::InvoiceLineRepository)
                singleOf(::InvoiceGenerator)
            },
        )
    }
}
