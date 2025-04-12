package org.simpleinvoice

import io.ktor.server.application.Application
import org.simpleinvoice.server.config.configureAdministration
import org.simpleinvoice.server.config.configureErrorHandling
import org.simpleinvoice.server.config.configureFrameworks
import org.simpleinvoice.server.config.configureHTTP
import org.simpleinvoice.server.config.configureRouting
import org.simpleinvoice.server.config.configureSecurity
import org.simpleinvoice.server.config.configureSerialization

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain
        .main(args)
}

fun Application.module() {
    configureFrameworks()
    configureSecurity()
    configureSerialization()
    configureHTTP()
    configureErrorHandling()
//    configureKafka()
//    configureDatabases()
    configureAdministration()
    configureRouting()
}
