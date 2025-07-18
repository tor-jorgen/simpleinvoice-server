package org.simpleinvoice.server.config

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond
import org.simpleinvoice.server.resources.model.ErrorCode
import org.simpleinvoice.server.resources.model.ErrorResponse
import org.simpleinvoice.server.resources.model.ErrorsResponse

fun Application.configureErrorHandling() {
    install(StatusPages) {
        statusFileWithLogging(
            code = HttpStatusCode.NotFound,
            logMessage = "The page does not exist!",
        )

        statusFileWithLogging(
            code = HttpStatusCode.Unauthorized,
            logMessage = "The user is not authorized to access this resource!",
        )

        statusFileWithLogging(
            code = HttpStatusCode.BadRequest,
            logMessage = "A bad request!",
        )

        exception<Throwable> { call, cause ->
            when {
                cause is BadRequestException -> {
                    logAndRespondWithResourceFile(
                        call = call,
                        status = HttpStatusCode.BadRequest,
                        cause = cause,
                        logMessage = "The request was malformed!",
                    )
                }
            }
            logAndRespondWithResourceFile(
                call = call,
                status = HttpStatusCode.InternalServerError,
                cause = cause,
                logMessage = "Internal server error!",
            )
        }
    }
}

private fun StatusPagesConfig.statusFileWithLogging(
    code: HttpStatusCode,
    logMessage: String? = null,
) {
    status(code) { call, status ->
        logAndRespondWithResourceFile(
            call = call,
            status = status,
            logMessage = logMessage,
        )
    }
}

private suspend fun logAndRespondWithResourceFile(
    call: ApplicationCall,
    status: HttpStatusCode,
    cause: Throwable? = null,
    logMessage: String? = null,
) {
    call.application.log.error("${status.value}/${status.description}: $logMessage @ ${call.request.local}", cause)
    call.response.status(status)
    call.respond(decodeCause(cause))
}

private fun decodeCause(cause: Throwable?): ErrorsResponse =
    if (cause?.message?.contains("violates foreign key constraint") == true) {
        ErrorsResponse(listOf(ErrorResponse(code = ErrorCode.FOREIGN_KEY_VIOLATION)))
    } else {
        ErrorsResponse(listOf(ErrorResponse(code = ErrorCode.GENERAL_ERROR)))
    }
