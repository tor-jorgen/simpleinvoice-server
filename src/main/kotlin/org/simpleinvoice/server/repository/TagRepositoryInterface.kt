package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.Tag
import java.util.UUID

interface TagRepositoryInterface {
    suspend fun all(activeOnly: Boolean): List<Tag>

    suspend fun upsert(
        tag: Tag,
        new: Boolean,
    ): Tag

    suspend fun delete(id: UUID): Boolean
}
