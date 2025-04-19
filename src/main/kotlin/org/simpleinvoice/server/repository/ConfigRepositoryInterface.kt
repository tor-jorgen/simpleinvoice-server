package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.Config

interface ConfigRepositoryInterface {
    suspend fun get(): Config

    suspend fun update(config: Config): Unit
}
