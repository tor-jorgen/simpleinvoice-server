package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.Tag
import java.util.UUID

interface TagRepositoryInterface {
    suspend fun all(activeOnly: Boolean): List<Tag>

    suspend fun upsert(
        tag: Tag,
        new: Boolean,
        message: String? = null,
    ): Tag

    suspend fun delete(
        id: UUID,
        message: String? = null,
    ): Boolean
}
