package org.simpleinvoice.server.resources.model

import kotlinx.serialization.Serializable

@Serializable
data class InfoResponse(
    val version: String,
)
