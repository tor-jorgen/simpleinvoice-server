package org.simpleinvoice.server.config

import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.koin.ktor.ext.get as getK

fun Application.runFlyway(config: DatabaseConfig = getK<DatabaseConfig>()) {
    log.debug("Running Flyway against: {}", config.connectionString)
    Flyway
        .configure()
        .dataSource(config.connectionString, config.user, config.password)
        .load()
        .migrate()
}

fun Application.configureDatabases(config: DatabaseConfig = getK<DatabaseConfig>()) {
    Database.connect(
        config.connectionString,
        user = config.user,
        password = config.password,
    )
}
