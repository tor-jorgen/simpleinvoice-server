package org.simpleinvoice.server.config

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.staticResources
import io.ktor.server.resources.Resources
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureBasicRouting() {
//    install(RequestValidation) {
//        validate<String> { bodyText ->
//            if (!bodyText.startsWith("Hello")) {
//                ValidationResult.Invalid("Body text should start with 'Hello'")
//            } else {
//                ValidationResult.Valid
//            }
//        }
//    }
    install(Resources)
    configureStaticResources()
    configurePublicRouting()
    configureSessionProtectedRouting()
}

/**
 * WARNING! These resources are open to anyone
 */
private fun Application.configureStaticResources() {
    routing {
        // These resources will be available from the root `resources/static`
        staticResources("/", "static")
    }
}

/**
 * WARNING! These routes are open to anyone
 */
private fun Application.configurePublicRouting() {
    routing {
        get("/about") {
            call.respondText(
                "This is a simple application for handling invoices. Please ask the administrator to be " +
                        "registered as a user if you want to use the application :-)",
                status = HttpStatusCode.OK,
            )
        }
    }
}

/**
 * These routes require a valid session, otherwise you have to log in
 */
private fun Application.configureSessionProtectedRouting() {

    routing {
//        authenticate(AUTH_SESSION) {
        // The home page (/ or /home)
        get(Regex("/($HOME)?")) {
            call.respondText(
                "Welcome home :-)",
                status = HttpStatusCode.OK,
            )
        }
    }
//    }
}
