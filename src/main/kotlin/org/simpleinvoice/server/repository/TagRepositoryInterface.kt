package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.Tag
import org.simpleinvoice.server.resources.model.TagsResponse
import java.util.UUID

interface TagRepositoryInterface {
    suspend fun all(activeOnly: Boolean): TagsResponse

    suspend fun upsert(
        tag: Tag,
        new: Boolean,
    ): Tag

    suspend fun delete(id: UUID): Boolean
}
