package org.simpleinvoice.server.resources.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import org.simpleinvoice.server.model.Tag
import java.util.UUID

@Serializable
@JsonIgnoreUnknownKeys
@kotlinx.serialization.ExperimentalSerializationApi
data class TagNoIdRequest(
    val name: String,
    val inactive: Boolean = false,
) {
    fun toTag(id: UUID): Tag =
        Tag(
            id = id,
            name = name,
            inactive = inactive,
        )
}
