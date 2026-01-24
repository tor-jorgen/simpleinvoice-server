package org.simpleinvoice.server.resources.model

import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.model.Tag
import java.util.UUID

@Serializable
data class TagRequestResponse(
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
            TagRequestResponse(
                id = tag.id,
                name = tag.name,
                inactive = tag.inactive,
            )
    }
}
