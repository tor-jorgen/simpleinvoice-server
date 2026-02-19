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
import org.simpleinvoice.server.config.handleEvents
import org.simpleinvoice.server.config.runFlyway
import org.simpleinvoice.server.resources.configureHouseholdsRouting
import org.simpleinvoice.server.resources.configureInvoicesRouting
import org.simpleinvoice.server.resources.configurePersonsRouting
import org.simpleinvoice.server.resources.configureProductsRouting
import org.simpleinvoice.server.resources.configureSettingsRouting
import org.simpleinvoice.server.resources.configureTagsRouting

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain
        .main(args)
}

@kotlinx.serialization.ExperimentalSerializationApi
fun Application.module() {
    configureDependencyInjection()
    handleEvents()
    configureHTTP()
    runFlyway()
    configureDatabases()
    configureSecurity()
    configureSerialization()
    configureErrorHandling()
    configureAdministration()
    configureBasicRouting()
    configureSettingsRouting()
    configureHouseholdsRouting()
    configurePersonsRouting()
    configureInvoicesRouting()
    configureProductsRouting()
    configureTagsRouting()
    configureOpenAPI()
}
