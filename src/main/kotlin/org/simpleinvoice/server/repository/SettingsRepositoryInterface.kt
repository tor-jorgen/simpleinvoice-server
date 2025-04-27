package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.Settings

interface SettingsRepositoryInterface {
    suspend fun get(): Settings

    fun getWithoutTransaction(): Settings

    suspend fun update(settings: Settings): Unit
}
