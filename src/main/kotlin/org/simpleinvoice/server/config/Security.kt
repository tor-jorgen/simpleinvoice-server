package org.simpleinvoice.server.config

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.auth.oauth
import io.ktor.server.auth.principal
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.csrf.CSRF
import io.ktor.server.request.uri
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.p
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.collections.set

private const val SCOPE_PUBLIC_PERSONAL_INFO = "https://www.googleapis.com/auth/userinfo.profile"
private const val SCOPE_OPEN_ID = "openid"

private const val QUERY_PARAM_REDIRECT_URL = "redirectUrl"

val httpClient =
    HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.extensions["SameSite"] = "lax"
//            cookie.maxAgeInSeconds = 3600
        }
    }

    val redirects = mutableMapOf<String, String>()
    authentication {
        oauth("auth-oauth-google") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("GOOGLE_CLIENT_ID"),
                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
                    defaultScopes = listOf(SCOPE_PUBLIC_PERSONAL_INFO),
                    // `offline` causes Google to send back a refresh token and an access token
                    extraAuthParameters = listOf("access_type" to "offline"),
                    onStateCreated = { call, state ->
                        // Save the redirect URL, so we can redirect back when the user has been authenticated
                        call.request.queryParameters[QUERY_PARAM_REDIRECT_URL]?.let {
                            redirects[state] = it
                        }
                    },
                )
            }
            client = httpClient
        }
    }

    install(CSRF) {
        // tests Origin is an expected value
        allowOrigin("http://localhost:8080")

        // tests Origin matches Host header
        originMatchesHost()

        // custom header checks
        checkHeader("X-CSRF-Token")
    }

    routing {
        authenticate("auth-oauth-google") {
            get("/login") {
                // Redirects to `authorizeUrl` automatically
//                call.respondRedirect("/callback")
            }

            get("/callback") {
                val currentPrincipal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                // redirects home if the url is not found before authorization
                currentPrincipal?.let { principal ->
                    principal.state?.let { state ->
                        call.sessions.set(UserSession(state, principal.accessToken))
                        redirects[state]?.let { redirect ->
                            // Redirect to the page the use originally asked for
                            call.respondRedirect(redirect)
                            return@get
                        }
                    }
                }
                call.respondRedirect("/home")
            }
        }

        get("/") {
            call.respondHtml {
                body {
                    p {
                        a("/login") { +"Login with Google" }
                    }
                }
            }
        }

        get("/home") {
            val userSession: UserSession? = getSession(call)
            if (userSession != null) {
                val userInfo: UserInfo = getGoogleUserInfo(httpClient, userSession)
                call.respondText("Hello, ${userInfo.name}! Welcome home!")
            }
        }

        get("/{path}") {
            val path = call.parameters["path"]
            val userSession: UserSession? = getSession(call)
            if (userSession != null) {
                val userInfo: UserInfo = getGoogleUserInfo(httpClient, userSession)
                call.respondText("Hello, ${userInfo.name}! Welcome to a non-existing page: $path")
            }
        }
    }
}

private suspend fun getSession(call: ApplicationCall): UserSession? = call.sessions.get() ?: redirectToLogin(call)

private suspend fun redirectToLogin(call: ApplicationCall): Nothing? {
    val redirectUrl =
        URLBuilder("http://0.0.0.0:8080/login").run {
            parameters.append(QUERY_PARAM_REDIRECT_URL, call.request.uri)
            build()
        }
    call.respondRedirect(redirectUrl)
    return null
}

private suspend fun getGoogleUserInfo(
    httpClient: HttpClient,
    userSession: UserSession,
): UserInfo =
    httpClient
        .get("https://www.googleapis.com/oauth2/v2/userinfo") {
            headers {
                append(HttpHeaders.Authorization, "Bearer ${userSession.accessToken}")
            }
        }.body()

@Serializable
data class UserSession(
    val state: String,
    val accessToken: String? = null,
    val count: Int = 0,
)

@Serializable
data class UserInfo(
    val id: String,
    val name: String,
    @SerialName("given_name") val givenName: String,
    @SerialName("family_name") val familyName: String,
    val picture: String? = null,
    val locale: String? = null,
)
