package org.simpleinvoice.server.config

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.http.content.resolveResource
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond
import io.ktor.server.response.respondText

fun Application.configureErrorHandling() {
    install(StatusPages) {
        statusFileWithLogging(
            code = HttpStatusCode.NotFound,
            filePath = "template404.html",
            logMessage = "Oh oh. This page does not exist!",
        )

        statusFileWithLogging(
            code = HttpStatusCode.Unauthorized,
            filePath = "template401.html",
            logMessage = "Oh oh. You don't have access to this page! Ask the administrator if you need access to this page",
        )

        statusFileWithLogging(
            code = HttpStatusCode.InternalServerError,
            filePath = "template500.html",
            logMessage =
                "Oh oh. Se application failed! Please retry. If that doesn't help, please contact the " +
                    "administrator, or if you are the administrator, report a bug",
        )

//        status(HttpStatusCode.NotFound) { call, status ->
//            call.application.log.info("${status.value}/${status.description}: ${call.request.local}")
//            call.respondText(text = "Oh oh. This page does not exist!", status = status)
//        }
//        statusFile(
//            HttpStatusCode.NotFound,
//            HttpStatusCode.Unauthorized,
//            HttpStatusCode.InternalServerError,
//            filePattern = "template#.html",
//        )

//        status(HttpStatusCode.Unauthorized) { call, status ->
//            call.application.log.info("${status.value}/${status.description}: ${call.request.local}")
//            call.respondText(
//                text = "Oh oh. You don't have access to this page! Ask the administrator if you need access to this page",
//                status = status,
//            )
//        }

        exception<Throwable> { call, cause ->
            call.application.log.error("Internal Server error!", cause)
            call.respondText(
                text =
                    "Oh oh. Se application failed! Please retry. If that doesn't help, please contact the " +
                        "administrator, or if you are the administrator, report a bug",
                status = HttpStatusCode.InternalServerError,
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
        call.application.log.info("${status.value}/${status.description}: $logMessage @ ${call.request.local}")
        val resource = call.resolveResource(filePath)
        if (resource == null) {
            call.response.status(status)
            call.respond("${status.value}/${status.description}: $logMessage")
        } else {
            call.response.status(status)
            call.respond(resource)
        }
    }
}
