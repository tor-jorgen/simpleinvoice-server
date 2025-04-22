package org.simpleinvoice.server

import io.ktor.server.application.Application
import org.simpleinvoice.server.config.configureAdministration
import org.simpleinvoice.server.config.configureBasicRouting
import org.simpleinvoice.server.config.configureDatabases
import org.simpleinvoice.server.config.configureDependencyInjection
import org.simpleinvoice.server.config.configureErrorHandling
import org.simpleinvoice.server.config.configureHTTP
import org.simpleinvoice.server.config.configureOpenAPI
import org.simpleinvoice.server.config.configureSecurity
import org.simpleinvoice.server.config.configureSerialization
import org.simpleinvoice.server.config.runFlyway
import org.simpleinvoice.server.resources.configureHouseholdsRouting
import org.simpleinvoice.server.resources.configureInvoiceLineRouting
import org.simpleinvoice.server.resources.configureInvoicesRouting
import org.simpleinvoice.server.resources.configurePersonsRouting
import org.simpleinvoice.server.resources.configureProductsRouting
import org.simpleinvoice.server.resources.configureSettingsRouting

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain
        .main(args)
}

fun Application.module() {
    configureHTTP()
    runFlyway()
    configureDatabases()
    configureDependencyInjection()
    configureSecurity()
    configureSerialization()
    configureErrorHandling()
//    configureKafka()
    configureAdministration()
    configureBasicRouting()
    configureSettingsRouting()
    configureHouseholdsRouting()
    configurePersonsRouting()
    configureInvoicesRouting()
    configureInvoiceLineRouting()
    configureProductsRouting()
    configureOpenAPI()
}
