pluginManagement {
    val kotlinVersion: String by settings
    val ktorVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        id("io.ktor.plugin") version ktorVersion
        kotlin("plugin.serialization") version kotlinVersion
    }
}

rootProject.name = "simpleinvoice-server"
