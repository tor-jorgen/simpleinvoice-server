package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImportHouseholdsRequest(
    @SerialName("lines_to_skip") val linesToSkip: Int,
    val households: String,
)
