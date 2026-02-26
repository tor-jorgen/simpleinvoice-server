val kotlinVersion: String by project
val javaLanguageVersion: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    kotlin("plugin.serialization")
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    id("de.undercouch.download") version "5.7.0"
    id("org.sonarqube") version "7.2.3.7755"
}

group = "org.simpleinvoice.server"
version = "1.1.0"

application {
    mainClass = "org.simpleinvoice.server.ApplicationKt"

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
    }

    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-cio")
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
    implementation("io.ktor:ktor-server-routing-openapi")
    implementation("io.ktor:ktor-server-openapi")

    val exposedVersion = "1.2.0"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")

    val h2Version = "2.4.240"
    implementation("com.h2database:h2:$h2Version")

    val koinVersion = "4.2.1"
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    val slf4jVersion = "2.0.17"
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    val logbackVersion = "1.5.32"
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    val postgresVersion = "42.7.10"
    implementation("org.postgresql:postgresql:$postgresVersion")

    val jacksonAnnotationsVersion = "2.21"
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonAnnotationsVersion")
    val jacksonVersion = "3.1.2"
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
    val jakartaMailVersion = "2.1.5"
    implementation("jakarta.mail:jakarta.mail-api:$jakartaMailVersion")

    val angusMailVersion = "2.0.5"
    implementation("org.eclipse.angus:angus-mail:$angusMailVersion")

    val flywayVersion = "12.3.0"
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:$flywayVersion")

    // Test

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")

    val assertjVersion = "3.27.7"
    testImplementation("org.assertj:assertj-core:$assertjVersion")

    val mockitoVersion = "5.23.0"
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    val mockitoKotlinVersion = "6.3.0"
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
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
    openApi {
        enabled = true
        codeInferenceEnabled = true
        onlyCommented = false
    }
}

// Download library from GitHub
tasks.register<de.undercouch.gradle.tasks.download.Download>("downloadOdt2pdf") {
    group = "build"
    description = "Downloads the odt2pdf library from GitHub releases."
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

tasks.shadowJar {
    isZip64 = true
    mergeServiceFiles()
    filesMatching("META-INF/services/**") {
        // Needed by Flyway, otherwise the files will overwrite each other
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

sonar {
    properties {
        property("sonar.organization", "tor-jorgen")
        property("sonar.projectKey", "tor-jorgen_simpleinvoice-server")
        property("sonar.projectName", "Simple Invoice Server")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}
