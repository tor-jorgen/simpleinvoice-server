package org.simpleinvoice.server.resources.model

import kotlinx.serialization.Serializable
import org.simpleinvoice.server.model.Tag
import java.util.UUID

@Serializable
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
