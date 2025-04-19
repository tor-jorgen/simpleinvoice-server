package org.simpleinvoice.server

import io.ktor.server.application.Application
import org.simpleinvoice.server.config.configureAdministration
import org.simpleinvoice.server.config.configureCallLogging
import org.simpleinvoice.server.config.configureDatabases
import org.simpleinvoice.server.config.configureErrorHandling
import org.simpleinvoice.server.config.configureFrameworks
import org.simpleinvoice.server.config.configureHTTP
import org.simpleinvoice.server.config.configureRouting
import org.simpleinvoice.server.config.configureSecurity
import org.simpleinvoice.server.config.configureSerialization
import org.simpleinvoice.server.config.runFlyway
import org.simpleinvoice.server.resources.configureConfigsRouting
import org.simpleinvoice.server.resources.configureHouseholdsRouting
import org.simpleinvoice.server.resources.configureInvoiceLineRouting
import org.simpleinvoice.server.resources.configureInvoicesRouting
import org.simpleinvoice.server.resources.configurePersonsRouting
import org.simpleinvoice.server.resources.configureProductsRouting

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain
        .main(args)
}

fun Application.module() {
    configureCallLogging()
    runFlyway()
    configureDatabases()
    configureFrameworks()
    configureSecurity()
    configureSerialization()
    configureHTTP()
    configureErrorHandling()
//    configureKafka()
    configureAdministration()
    configureRouting()
    configureConfigsRouting()
    configureHouseholdsRouting()
    configurePersonsRouting()
    configureInvoicesRouting()
    configureInvoiceLineRouting()
    configureProductsRouting()
}
