package org.simpleinvoice.server.config

import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.flywaydb.core.Flyway
import org.flywaydb.core.extensibility.Plugin
import org.flywaydb.core.internal.plugin.PluginRegister
import org.flywaydb.core.internal.resource.CoreResourceTypeProvider
import org.jetbrains.exposed.sql.Database
import org.koin.ktor.ext.get as getK

private const val REGISTERED_PLUGINS_FIELD = "REGISTERED_PLUGINS"

fun Application.runFlyway(config: DatabaseConfig = getK<DatabaseConfig>()) {
    log.debug("Running Flyway against: {}", config.connectionString)
    Flyway
        .configure()
        .dataSource(config.connectionString, config.user, config.password)
        .validateMigrationNaming(true)
        .load()
        .apply {
//            if (config.applyUnrecognizedMigrationFileFormatFix) {
            registerCoreResourceTypeProviderIfMissing()
            log.info("*** Applying unrecognized migration file format fix")
//            }
        }.migrate()
}

fun Application.configureDatabases(config: DatabaseConfig = getK<DatabaseConfig>()) {
    Database.connect(
        config.connectionString,
        user = config.user,
        password = config.password,
    )
}

// See https://github.com/flyway/flyway/issues/4112 for more information
private fun Flyway.registerCoreResourceTypeProviderIfMissing(): Flyway =
    apply {
        val pluginRegister = this.configuration.pluginRegister
        val field = PluginRegister::class.java.getDeclaredField(REGISTERED_PLUGINS_FIELD)
        field.setAccessible(true)
        @Suppress("UNCHECKED_CAST")
        val pluginList = (field.get(pluginRegister) as ArrayList<Plugin>)
        if (pluginList.none { plugin -> plugin is CoreResourceTypeProvider }) {
            pluginList.add(CoreResourceTypeProvider())
        }
    }
