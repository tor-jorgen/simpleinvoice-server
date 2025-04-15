package com.example.org.simpleinvoice.server.config

import io.ktor.server.application.Application
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

fun runFlyway() {
    Flyway
        .configure()
        .dataSource("jdbc:postgresql://localhost:5432/simple_invoice", "admin", "hugo")
        .load()
        .migrate()
}

fun Application.configureDatabases() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/simple_invoice",
        user = "admin",
        password = "hugo",
    )
}
