package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.User
import org.simpleinvoice.server.resources.model.UsersResponse
import java.util.UUID

interface UserRepositoryInterface {
    suspend fun all(): UsersResponse

    suspend fun upsert(
        user: User,
        new: Boolean,
    ): User

    fun upsertWithoutTransaction(user: User): User

    suspend fun delete(id: UUID): Boolean
}
