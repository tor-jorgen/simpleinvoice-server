package org.simpleinvoice.server.config

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.staticResources
import io.ktor.server.resources.Resources
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import org.simpleinvoice.resources.Customers

fun Application.configureRouting() {
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
        authenticate(AUTH_SESSION) {
            // The home page (/ or /home)
            get(Regex("/($HOME)?")) {
                call.respondText(
                    "Welcome home :-)",
                    status = HttpStatusCode.OK,
                )
            }

            get<Customers> { customer ->
                // Get all customers
                call.respond("List of articles sorted starting from $customer")
            }
            get<Customers.New> { customer ->
                // Show a page with fields for creating a new customer
                call.respond("List of articles sorted starting from $customer")
            }
            post<Customers> { customer ->
                // Save a customer
                call.respondText("An article is saved", status = HttpStatusCode.Created)
            }
            get<Customers.Id> { customer ->
                // Show a customer with id ${customer.id}
                call.respondText("An article with id ${customer.id}", status = HttpStatusCode.OK)
            }
            get<Customers.Id.Edit> { customer ->
                // Show a page with fields for editing a customer
                call.respondText("Edit an article with id ${customer.parent.id}", status = HttpStatusCode.OK)
            }
            put<Customers.Id> { customer ->
                // Update a customer
                call.respondText("An article with id ${customer.id} updated", status = HttpStatusCode.OK)
            }
            delete<Customers.Id> { article ->
                // Delete a customer
                call.respondText("An article with id ${article.id} deleted", status = HttpStatusCode.OK)
            }
        }
    }
}
