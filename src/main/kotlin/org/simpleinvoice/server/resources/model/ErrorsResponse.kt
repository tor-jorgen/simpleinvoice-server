package org.simpleinvoice.server.resources.model

import kotlinx.serialization.Serializable

@Serializable
enum class ErrorCode {
    GENERAL_ERROR,
    FOREIGN_KEY_VIOLATION,
}

@Serializable
data class ErrorsResponse(
    val errors: List<ErrorResponse>,
)

@Serializable
data class ErrorResponse(
    val code: ErrorCode,
)
