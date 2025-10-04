package org.simpleinvoice.server.config

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.csrf.CSRF
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.util.toMap
import org.koin.ktor.ext.get as getK

private const val CSRF_HEADER = "X-SIMPLEINVOICE-CSRF-TOKEN"

fun Application.configureHTTP(config: SecurityConfig = getK<SecurityConfig>()) {
    install(CORS) {
        config.allowHostsAndSchemas().forEach { (host, schemes) ->
            allowHost(host = host, schemes = schemes)
        }
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(CSRF_HEADER)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowCredentials = true
    }

    /**
     * CSRF protection.
     *
     * This will only be performed on state-changing requests (POST, PUT, DELETE, PATCH)
     */
    install(CSRF) {
        config.allowHosts.forEach { allowOrigin(it) }

        // Tests Origin matches Host
        //        originMatchesHost()

        // Custom header checks
        checkHeader(CSRF_HEADER) { header ->
            // Check if the header value is a valid CSRF token
            header == config.csrfToken
        }
    }

    install(CallLogging) {
        format { call ->
            val method = call.request.httpMethod.value
            val uri = call.request.uri
            val requestHeaders = call.request.headers.toMap()
            val queryParams = call.request.queryParameters.toMap()
            val status = call.response.status() ?: "Unhandled"
            val responseHeaders =
                call.response.headers
                    .allValues()
                    .toMap()
            "HTTP $method $uri | Status: $status | Request Headers: $requestHeaders | QueryParams: $queryParams " +
                "| Response Headers: $responseHeaders"
        }
    }

//    install(CORS) {
//        anyMethod() // Allow all HTTP methods
// //        anyHost() // Allow requests from any origin
//        allowCredentials = true // Allow credentials
//        allowNonSimpleContentTypes = true // Allow non-simple content types
// //        allowHost(host = "localhost", schemes = listOf("http", "https")) // Allow localhost:3000
// //        allowOrigins { origin ->
// //            origin.equals("http://localhost:3000", true)
// //        }
//        allowSameOrigin = true
//    }

//    install(HttpsRedirect) {
//        // The port to redirect to. By default 443, the default HTTPS port.
//        sslPort = 443
//        // 301 Moved Permanently, or 302 Found redirect.
//        permanentRedirect = true
//    }
}
