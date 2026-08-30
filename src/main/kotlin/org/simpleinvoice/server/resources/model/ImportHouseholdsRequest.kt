package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImportHouseholdsRequest(
    @SerialName("lines_to_skip") val linesToSkip: Int,
    val tags: List<TagRequest>,
    val households: String,
    val message: String? = null,
)
