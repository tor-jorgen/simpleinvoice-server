package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.User
import java.util.UUID

interface UserRepositoryInterface {
    suspend fun all(): List<User>

    suspend fun add(user: User): Unit

    suspend fun update(user: User): Unit

    suspend fun delete(id: UUID): Boolean
}
