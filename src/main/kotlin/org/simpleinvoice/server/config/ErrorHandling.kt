package org.simpleinvoice.server.config

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.http.content.resolveResource
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

fun Application.configureErrorHandling() {
    install(StatusPages) {
        statusFileWithLogging(
            code = HttpStatusCode.NotFound,
            filePath = "template404.html",
            logMessage = "The page does not exist!",
        )

        statusFileWithLogging(
            code = HttpStatusCode.Unauthorized,
            filePath = "template401.html",
            logMessage = "The user is not authorized to access this resource!",
        )

        exception<Throwable> { call, cause ->
            when {
                cause is BadRequestException -> {
                    logAndRespondWithResourceFile(
                        call = call,
                        status = HttpStatusCode.BadRequest,
                        filePath = "template400.html",
                        cause = cause,
                        logMessage = "The request was malformed!"
                    )
                }
            }
            logAndRespondWithResourceFile(
                call = call,
                status = HttpStatusCode.InternalServerError,
                filePath = "template500.html",
                cause = cause,
                logMessage = "Internal server error!"
            )
        }
    }
}

private fun StatusPagesConfig.statusFileWithLogging(
    code: HttpStatusCode,
    filePath: String,
    logMessage: String? = null,
) {
    status(code) { call, status ->
        logAndRespondWithResourceFile(
            call = call,
            status = status,
            filePath = filePath,
            logMessage = logMessage,
        )
    }
}

private suspend fun logAndRespondWithResourceFile(
    call: ApplicationCall,
    status: HttpStatusCode,
    filePath: String,
    cause: Throwable? = null,
    logMessage: String? = null,
) {
    call.application.log.error("${status.value}/${status.description}: $logMessage @ ${call.request.local}", cause)
    val resource = call.resolveResource(filePath)
    if (resource == null) {
        call.response.status(status)
        call.respond("${status.value}/${status.description}: $logMessage")
    } else {
        call.response.status(status)
        call.respond(resource)
    }
}
