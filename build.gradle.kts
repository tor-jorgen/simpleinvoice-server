val h2_version: String by project
val koin_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val postgres_version: String by project

plugins {
    kotlin("jvm") version "2.1.20"
    id("io.ktor.plugin") version "3.1.2"
    kotlin("plugin.serialization") version "2.1.20"
    id("org.flywaydb.flyway") version "10.0.0"
}

group = "org.simpleinvoice.server"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.cio.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://packages.confluent.io/maven/") }
}

dependencies {
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-cio")
    implementation("io.ktor:ktor-server-csrf")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-sessions")
    implementation("io.ktor:ktor-server-auto-head-response")
    implementation("io.ktor:ktor-server-request-validation")
    implementation("io.ktor:ktor-server-resources")
    implementation("io.ktor:ktor-server-host-common")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-http-redirect")
    implementation("io.ktor:ktor-server-html-builder")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-config-yaml")

    implementation("io.ktor:ktor-serialization-kotlinx-json")
//    implementation("io.ktor:ktor-serialization-jackson")

    val exposedVersion = "0.61.0"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")

    val kafkaVersion = "2.1.2"
    implementation("io.github.flaxoos:ktor-server-kafka:$kafkaVersion")
//    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("com.h2database:h2:$h2_version")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Database migration
    val postgresVersion = "42.7.5"
    implementation("org.postgresql:postgresql:$postgresVersion")
    val flywayVersion = "11.7.0"
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:$flywayVersion")

    val ktorOpenApiVersion = "5.0.2"
    implementation("io.github.smiley4:ktor-openapi:$ktorOpenApiVersion")

    val jacksonVersion = "2.19.0"
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    val odfDomVersion = "0.12.0"
    implementation("org.odftoolkit:odfdom-java:$odfDomVersion")
    val simpleOdfVersion = "0.9.0"
    implementation("org.odftoolkit:simple-odf:$simpleOdfVersion")
    implementation("org.odt2pdf", "odt2pdf", "1.0", classifier = "all")

    val javaxMail = "1.6.2"
    implementation("com.sun.mail:javax.mail:$javaxMail")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

ktor {
    docker {
        localImageName.set("simpleinvoice-server")
    }
}

// flyway {
//    skipExecutingMigrations = true
//    url = "jdbc:postgresql://localhost:5432/simple_invoice"
//    user = "admin"
// }
