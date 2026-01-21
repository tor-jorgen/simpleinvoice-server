package org.simpleinvoice.server.config

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.cors.routing.CORS
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
        maxAgeInSeconds = 3600 // Tell browser to cache preflight response for 1 hour to avoid unnecessary traffic
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

//    install(HttpsRedirect) {
//        // The port to redirect to. By default 443, the default HTTPS port.
//        sslPort = 443
//        // 301 Moved Permanently, or 302 Found redirect.
//        permanentRedirect = true
//    }
}
