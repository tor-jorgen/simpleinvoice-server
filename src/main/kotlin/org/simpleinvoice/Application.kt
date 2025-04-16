package org.simpleinvoice

import com.example.org.simpleinvoice.resources.configureHouseholdsRouting
import com.example.org.simpleinvoice.resources.configureInvoiceLineRouting
import com.example.org.simpleinvoice.resources.configureInvoicesRouting
import com.example.org.simpleinvoice.resources.configurePersonsRouting
import com.example.org.simpleinvoice.resources.configureProductsRouting
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
    configureHouseholdsRouting()
    configurePersonsRouting()
    configureInvoicesRouting()
    configureInvoiceLineRouting()
    configureProductsRouting()
}
