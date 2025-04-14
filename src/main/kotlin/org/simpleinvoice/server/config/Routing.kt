package org.simpleinvoice.server.config

import com.example.org.simpleinvoice.resources.CustomerCreate
import com.example.org.simpleinvoice.resources.CustomerUpdate
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receive
import io.ktor.server.resources.Resources
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
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
//        authenticate(AUTH_SESSION) {
        // The home page (/ or /home)
        get(Regex("/($HOME)?")) {
            call.respondText(
                "Welcome home :-)",
                status = HttpStatusCode.OK,
            )
        }

        get<Customers> { customer ->
            // Get all customers
            call.respond("All customers are fetched: $customer")
        }
//        get<Customers.New> { customer ->
//            // Show a page with fields for creating a new customer
//            call.respond("List of articles sorted starting from $customer")
//        }
        post<Customers> {
            // Save a customer
            val customer = call.receive<CustomerCreate>()
            call.respondText("$customer is saved", status = HttpStatusCode.Created)
        }
        get<Customers.Id> { request ->
            // Show a customer with id ${customer.id}
            call.respondText("An article with id ${request.id} is fetched", status = HttpStatusCode.OK)
        }
//        get<Customers.Id.Edit> { customer ->
//            // Show a page with fields for editing a customer
//            call.respondText("Edit an article with id ${customer.parent.id}", status = HttpStatusCode.OK)
//        }
        put<Customers.Id> { request ->
            // Update a customer
            val customer = call.receive<CustomerUpdate>()
            call.respondText("$customer with id ${request.id} updated", status = HttpStatusCode.OK)
        }
        delete<Customers.Id> { request ->
            // Delete a customer
            call.respondText("A customer with id ${request.id} deleted", status = HttpStatusCode.OK)
        }
    }
//    }
}
