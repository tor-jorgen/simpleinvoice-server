package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.Product
import java.util.UUID

interface ProductRepositoryInterface {
    suspend fun all(activeOnly: Boolean): List<Product>

    suspend fun byIds(ids: List<UUID>): List<Product>

    suspend fun upsert(
        product: Product,
        new: Boolean,
    ): Product

    suspend fun delete(id: UUID): Boolean
}
