package org.simpleinvoice.server.resources.model

import kotlinx.serialization.Serializable

@Serializable
data class ListResponse<T>(
    val data: List<T>,
)
