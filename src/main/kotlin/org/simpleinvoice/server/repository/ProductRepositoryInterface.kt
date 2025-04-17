package org.simpleinvoice.server.repository

import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.simpleinvoice.server.model.Product
import java.util.UUID

interface ProductRepositoryInterface {
    suspend fun all(): List<Product>

    suspend fun upsert(product: Product): UpsertStatement<Long>

    fun upsertWithoutTransaction(product: Product): UpsertStatement<Long>

    suspend fun delete(id: UUID): Boolean
}
