package org.simpleinvoice.server.resources.model

import kotlinx.serialization.Serializable
import org.simpleinvoice.server.model.Tag
import java.util.UUID

@Serializable
data class TagRequest(
    val item: TagDTO,
    val message: String? = null,
) {
    fun toTag(): Tag = item.toTag()
}

@Serializable
data class TagRequestDTO(
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
