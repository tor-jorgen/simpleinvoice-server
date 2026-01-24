package org.simpleinvoice.server.resources.model

import kotlinx.serialization.Serializable

@Serializable
data class TagsResponse(
    val tags: List<TagRequestResponse>,
)
