val kotlinVersion: String by project
val javaLanguageVersion: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    kotlin("plugin.serialization")
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
    id("de.undercouch.download") version "5.6.0"
    // The latest version (7.2.2.6593) fails dependency validation
    id("org.sonarqube") version "7.1.0.6387"
}

group = "org.simpleinvoice.server"
version = "1.0.0"

application {
    mainClass = "io.ktor.server.cio.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

val odt2pdfVersion = "1.0.0"
val odt2pdfJar = "./.libs/odt2pdf-$odt2pdfVersion-all.jar"

dependencies {

    constraints {
        implementation("commons-beanutils:commons-beanutils:1.11.0") {
            because("CVE-2025-48734")
        }
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    }

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

    val h2Version = "2.4.240"
    implementation("com.h2database:h2:$h2Version")

    val koinVersion = "4.1.1"
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    val slf4jVersion = "2.0.17"
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    val logbackVersion = "1.5.25"
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    val postgresVersion = "42.7.9"
    implementation("org.postgresql:postgresql:$postgresVersion")

    val ktorOpenApiVersion = "5.4.0"
    implementation("io.github.smiley4:ktor-openapi:$ktorOpenApiVersion")

    val jacksonVersion = "3.0.3"
    implementation("tools.jackson.core:jackson-databind:$jacksonVersion")
    implementation("tools.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    implementation("tools.jackson.module:jackson-module-kotlin:$jacksonVersion")

    // Document conversion
    // `odfdom-java` has vulnerabilities, but this is the last version
    val odfDomVersion = "0.12.0"
    implementation("org.odftoolkit:odfdom-java:$odfDomVersion")

    val simpleOdfVersion = "0.9.0"
    implementation("org.odftoolkit:simple-odf:$simpleOdfVersion")

    implementation(files(layout.projectDirectory.file(odt2pdfJar)))

    // Email
    val javaxMailVersion = "2.0.2"
    implementation("com.sun.mail:jakarta.mail:$javaxMailVersion")

    // > 11.12.0 does not work in a fatjar
    val flywayVersion = "11.12.0"
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:$flywayVersion")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(javaLanguageVersion))
    }
}

ktor {
    docker {
        localImageName.set("simpleinvoice-server")
    }
}

// Download library from GitHub
tasks.register<de.undercouch.gradle.tasks.download.Download>("downloadOdt2pdf") {
    src("https://github.com/tor-jorgen/odt2pdf/releases/download/v$odt2pdfVersion/odt2pdf-$odt2pdfVersion-all.jar")
    dest(layout.projectDirectory.file(odt2pdfJar))
    overwrite(false)
}

tasks.register("printVersion") {
    group = "help"
    description = "Prints the project version."

    doLast {
        println(project.version)
    }
}

tasks.named("compileKotlin") {
    dependsOn("downloadOdt2pdf")
}

sonar {
    properties {
        property("sonar.organization", "tor-jorgen")
        property("sonar.projectKey", "tor-jorgen_simpleinvoice-server")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}
