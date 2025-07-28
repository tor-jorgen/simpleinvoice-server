val kotlinVersion: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    kotlin("plugin.serialization")
//    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
    id("de.undercouch.download") version "5.6.0"
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
}

val odt2pdfVersion = "1.0.0"
val odt2pdfJar = "./.libs/odt2pdf-$odt2pdfVersion-all.jar"

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

    val h2Version = "2.3.232"
    implementation("com.h2database:h2:$h2Version")

    val koinVersion = "4.1.0"
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    val slf4jVersion = "2.0.17"
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    val logbackVersion = "1.5.18"
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    val postgresVersion = "42.7.7"
    implementation("org.postgresql:postgresql:$postgresVersion")

    val ktorOpenApiVersion = "5.1.0"
    implementation("io.github.smiley4:ktor-openapi:$ktorOpenApiVersion")

    val jacksonVersion = "2.19.2"
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    // Document conversion
    // `odfdom-java` has vulnerabilities, but this is the last version
    val odfDomVersion = "0.12.0"
    implementation("org.odftoolkit:odfdom-java:$odfDomVersion")

    val simpleOdfVersion = "0.9.0"
    implementation("org.odftoolkit:simple-odf:$simpleOdfVersion")

    implementation(files(layout.projectDirectory.file(odt2pdfJar)))

    // Email
    val javaxMailVersion = "1.6.2"
    implementation("com.sun.mail:javax.mail:$javaxMailVersion")

    val flywayVersion = "11.10.3"
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:$flywayVersion")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}


ktor {
    docker {
        localImageName.set("simpleinvoice-server")
    }
}

// Download library from GitHub
tasks.register<de.undercouch.gradle.tasks.download.Download>("downloadOdt2pdf") {
    src("https://github.com/tor-jorgen/odt2pdf/releases/download/v1.0.0/odt2pdf-$odt2pdfVersion-all.jar")
    dest(layout.projectDirectory.file(odt2pdfJar))
    overwrite(false)
}

tasks.named("compileKotlin") {
    dependsOn("downloadOdt2pdf")
}

//ktlint {
//    additionalEditorconfig.set(
//        mapOf(
//            "max_line_length" to "120",
//        ),
//    )
//}
