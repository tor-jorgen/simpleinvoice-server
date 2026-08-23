package org.simpleinvoice.server.config

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.smithy.kotlin.runtime.net.url.Url
import io.ktor.server.application.Application
import io.ktor.server.application.install
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.odt2pdf.PDFConverter
import org.simpleinvoice.server.invoice.DocumentGenerator
import org.simpleinvoice.server.invoice.EmailGenerator
import org.simpleinvoice.server.invoice.EventPublisher
import org.simpleinvoice.server.invoice.HouseholdImporter
import org.simpleinvoice.server.invoice.InvoiceBulkUpdater
import org.simpleinvoice.server.invoice.InvoiceConfig
import org.simpleinvoice.server.invoice.InvoiceGenerator
import org.simpleinvoice.server.model.AuditTrail
import org.simpleinvoice.server.repository.AuditTrailRepository
import org.simpleinvoice.server.repository.HouseholdRepository
import org.simpleinvoice.server.repository.InvoiceLineRepository
import org.simpleinvoice.server.repository.InvoiceRepository
import org.simpleinvoice.server.repository.PersonRepository
import org.simpleinvoice.server.repository.ProductRepository
import org.simpleinvoice.server.repository.SettingsRepository
import org.simpleinvoice.server.repository.TagRepository
import org.simpleinvoice.server.repository.UserRepository
import org.simpleinvoice.server.util.s3.S3StorageClient
import org.simpleinvoice.server.util.s3.StorageClient
import org.simpleinvoice.server.util.smtp.SmtpClient
import org.simpleinvoice.server.util.smtp.SmtpConfig

fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        modules(
            module {
                val dbConnectionPrefix = property("db.connectionPrefix")
                val dbPort = property("db.port")
                val dbName = property("db.name")
                single {
                    DatabaseConfig(
                        connectionString = "$dbConnectionPrefix:$dbPort/$dbName",
                        user = property("db.user"),
                        password = property("db.password"),
                    )
                }

                val invoiceBucketName = property("invoice.invoiceBucketName")
                val configBucketName = property("invoice.configBucketName")
                single {
                    InvoiceConfig(
                        invoiceBucketName = invoiceBucketName,
                        configBucketName = configBucketName,
                        invoiceTemplateName = property("invoice.invoiceTemplateName"),
                        invoiceName = property("invoice.invoiceName"),
                    )
                }

                single {
                    SmtpConfig(
                        host = property("smtp.host"),
                        port = property("smtp.port").toInt(),
                        tls = property("smtp.tls").toBoolean(),
                        username = property("smtp.username"),
                        password = property("smtp.password"),
                        senderEmail = property("smtp.senderEmail"),
                        senderName = property("smtp.senderName"),
                    )
                }

                single {
                    SecurityConfig(
                        clientId = property("security.clientId"),
                        clientSecret = property("security.clientSecret"),
                        allowHosts = property("security.allowHosts").split(",", ";", " ", "\t").map { it.trim() },
                    )
                }

                single {
                    PDFConverter()
                }

                single {
                    Channel<AuditTrail>(capacity = 100)
                }

                val s3ConnectionPrefix = property("s3.connectionPrefix")
                val s3Port = property("s3.port")
                val s3AccessKeyId = property("s3.accessKeyId")
                val s3SecretAccessKey = property("s3.secretAccessKey")

                single {
                    runBlocking {
                        val url = "$s3ConnectionPrefix:$s3Port"
                        S3Client.fromEnvironment {
                            endpointUrl = Url.parse(url)
                            region = "eu-west-1"
                            forcePathStyle = true // Needed by MinIO
                            credentialsProvider =
                                StaticCredentialsProvider {
                                    accessKeyId = s3AccessKeyId
                                    secretAccessKey = s3SecretAccessKey
                                }
                        }
                    }
                }

                single {
                    S3StorageClient(
                        s3Client = get(),
                    ).also {
                        runBlocking {
                            try {
                                it.ensureBucketExists(invoiceBucketName)
                                it.ensureBucketExists(configBucketName)
                            } catch (e: Exception) {
                                throw e
                            }
                        }
                    }
                } bind StorageClient::class

                singleOf(::SmtpClient)
                singleOf(::EventPublisher)
                singleOf(::SettingsRepository)
                singleOf(::UserRepository)
                singleOf(::HouseholdRepository)
                singleOf(::PersonRepository)
                singleOf(::ProductRepository)
                singleOf(::InvoiceRepository)
                singleOf(::InvoiceLineRepository)
                singleOf(::AuditTrailRepository)
                singleOf(::TagRepository)
                singleOf(::InvoiceGenerator)
                singleOf(::DocumentGenerator)
                singleOf(::EmailGenerator)
                singleOf(::HouseholdImporter)
                singleOf(::InvoiceBulkUpdater)
            },
        )
    }
}

private fun Application.property(name: String): String = environment.config.propertyOrNull(name)?.getString()!!
