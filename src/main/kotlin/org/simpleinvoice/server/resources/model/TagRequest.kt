package org.simpleinvoice.server.resources.model

import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.model.Tag
import java.util.UUID

@Serializable
class TagRequest(
    @Serializable(with = UUIDSerializer::class) val id: UUID? = null,
    val name: String,
    val inactive: Boolean,
) {
    fun toTag(id: UUID): Tag =
        Tag(
            id = id,
            name = name,
            inactive = inactive,
        )
}
