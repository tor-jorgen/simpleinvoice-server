package org.simpleinvoice

import io.ktor.server.application.Application
import org.simpleinvoice.server.config.configureAdministration
import org.simpleinvoice.server.config.configureDatabases
import org.simpleinvoice.server.config.configureErrorHandling
import org.simpleinvoice.server.config.configureFrameworks
import org.simpleinvoice.server.config.configureHTTP
import org.simpleinvoice.server.config.configureRouting
import org.simpleinvoice.server.config.configureSecurity
import org.simpleinvoice.server.config.configureSerialization
import org.simpleinvoice.server.config.runFlyway
import org.simpleinvoice.server.routing.configureHouseholdsRouting
import org.simpleinvoice.server.routing.configurePersonsRouting

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain
        .main(args)
}

fun Application.module() {
    runFlyway()
    configureDatabases()
    configureFrameworks()
    configureSecurity()
    configureSerialization()
    configureHTTP()
    configureErrorHandling()
//    configureKafka()
//    configureDatabases()
    configureAdministration()
    configureRouting()
    configurePersonsRouting()
    configureHouseholdsRouting()
}
