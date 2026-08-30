package org.simpleinvoice.server.resources.model

import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.model.Tag
import java.util.UUID

/**
 * Request and response object for a tag that is a child of another object
 */
@Serializable
data class TagDTO(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val name: String,
    val inactive: Boolean = false,
) {
    fun toTag(): Tag =
        Tag(
            id = id,
            name = name,
            inactive = inactive,
        )

    companion object {
        fun fromTag(tag: Tag) =
            TagDTO(
                id = tag.id,
                name = tag.name,
                inactive = tag.inactive,
            )
    }
}
