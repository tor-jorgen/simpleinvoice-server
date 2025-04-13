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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.collections.set

private const val SCOPE_PUBLIC_PERSONAL_INFO = "https://www.googleapis.com/auth/userinfo.profile"
private const val SCOPE_EMAIL = "https://www.googleapis.com/auth/userinfo.email"
private const val SCOPE_OPEN_ID = "openid"

private const val QUERY_PARAM_REDIRECT_URL = "redirectUrl"

private const val URL_LOGIN = "/login"
private const val URL_LOGIN_GOOGLE = "/logingoogle"
private const val URL_CALLBACK = "/callback"
private const val URL_HOME = "/home"

private const val AUTH_OAUTH_GOOGLE = "auth-oauth-google"

val httpClient =
    HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

// TODO: Use discovery endpoint
// https://accounts.google.com/.well-known/openid-configuration

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.extensions["SameSite"] = "lax"
//            cookie.maxAgeInSeconds = 3600
        }
    }

    val redirects = mutableMapOf<String, String>()
    authentication {
        oauth(AUTH_OAUTH_GOOGLE) {
            urlProvider = { "http://localhost:8080$URL_CALLBACK" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("GOOGLE_CLIENT_ID"),
                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
                    defaultScopes = listOf(SCOPE_EMAIL, SCOPE_PUBLIC_PERSONAL_INFO, SCOPE_OPEN_ID),
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
        authenticate(AUTH_OAUTH_GOOGLE) {
            get(URL_LOGIN_GOOGLE) {
                // Redirects to `authorizeUrl` automatically
            }

            // Handle callback from resource server
            get(URL_CALLBACK) {
                println("URL: $URL_CALLBACK")
                val currentPrincipal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                // redirects home if the url is not found before authorization
                currentPrincipal?.let { principal ->
                    principal.state?.let { state ->
                        call.sessions.set(UserSession.fromGoogle(principal, state))
                        println("Principal: $principal")
                        redirects[state]?.let { redirect ->
                            // Redirect to the page the use originally asked for
                            call.respondRedirect(redirect)
                            return@get
                        }
                    }
                }
                call.respondRedirect(URL_HOME)
            }
        }

//        get("""/(login)?""".toRegex()) {
        get(URL_LOGIN) {
            println("URL: /(login)?")
            val userSession: UserSession? = call.sessions.get()
            if (userSession == null) {
                call.respondHtml {
                    body {
                        p {
                            a(URL_LOGIN_GOOGLE) { +"Login with Google" }
                        }
                    }
                }
            } else {
                call.respondRedirect(URL_HOME)
            }
        }

        get(URL_HOME) {
            println("URL: /home")
            val userSession: UserSession? = getSessionOrLogin(call)
            if (userSession != null) {
//                val userInfo: String = getGoogleUserInfo(httpClient, userSession)
//                call.respondText("Hello, $userInfo! Welcome home!")
//                throw RuntimeException()
                call.respondText("Hello, ${userSession.toJson()}! Welcome home!")
            }
        }
    }
}

private suspend fun getSessionOrLogin(call: ApplicationCall): UserSession? = call.sessions.get() ?: redirectToLogin(call)

private suspend fun redirectToLogin(call: ApplicationCall): Nothing? {
    println("URL: Redirect to login")
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
): String =
    httpClient
        .get("https://www.googleapis.com/oauth2/v2/userinfo") {
            headers {
                append(HttpHeaders.Authorization, "Bearer ${userSession.accessToken}")
            }
        }.body()

@Serializable
private data class UserSession(
    val state: String,
    val tokenType: String? = null,
    val expiresIn: Long? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val idToken: String? = null,
    val scope: String? = null,
    val count: Int = 0,
) {
    fun toJson() = Json.encodeToJsonElement(this).toString()

    companion object {
        fun fromGoogle(
            principal: OAuthAccessTokenResponse.OAuth2,
            state: String,
        ): UserSession =
            UserSession(
                state = state,
                accessToken = principal.accessToken,
                tokenType = principal.tokenType,
                expiresIn = principal.expiresIn,
                refreshToken = principal.refreshToken,
                scope = principal.extraParameters["scope"],
                idToken = principal.extraParameters["id_token"],
            )
    }
}

@Serializable
private data class GoogleUserInfo(
    val id: String,
    val email: String? = null,
    @SerialName("verified_email") val verifiedEmail: Boolean? = null,
    val name: String,
    @SerialName("given_name") val givenName: String,
    @SerialName("family_name") val familyName: String,
    @SerialName("picture") val pictureURL: String? = null,
    @SerialName("hd") val domain: String? = null,
) {
    fun toUserInfo(): UserInfo =
        UserInfo(
            id = id,
            name = name,
            givenName = givenName,
            familyName = familyName,
            pictureURL = pictureURL,
        )
}

private data class UserInfo(
    val id: String,
    val name: String,
    val givenName: String,
    val familyName: String,
    val pictureURL: String? = null,
    val locale: String? = null,
)
