package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.Config
import java.util.UUID

interface ConfigRepositoryInterface {
    suspend fun all(): List<Config>

    suspend fun add(config: Config): Unit

    suspend fun update(config: Config): Unit

    suspend fun delete(id: UUID): Boolean
}
