package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.User
import java.util.UUID

interface UserRepositoryInterface {
    suspend fun all(): List<User>

    suspend fun upsert(
        user: User,
        new: Boolean,
        message: String? = null,
    ): User

    suspend fun delete(
        id: UUID,
        message: String? = null,
    ): Boolean
}
