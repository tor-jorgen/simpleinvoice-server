pluginManagement {
    val kotlinVersion: String = providers.gradleProperty("kotlinVersion").get()
    val ktorVersion: String = providers.gradleProperty("ktorVersion").get()

    plugins {
        kotlin("jvm") version kotlinVersion
        id("io.ktor.plugin") version ktorVersion
        kotlin("plugin.serialization") version kotlinVersion
    }
}

rootProject.name = "simpleinvoice-server"
