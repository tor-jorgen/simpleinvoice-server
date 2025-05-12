package org.simpleinvoice.server.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.upsert
import org.simpleinvoice.server.invoice.EventPublisher
import org.simpleinvoice.server.model.Product
import org.simpleinvoice.server.model.Tag
import org.simpleinvoice.server.repository.model.ProductDAO
import org.simpleinvoice.server.repository.model.ProductTable
import org.simpleinvoice.server.repository.model.ProductTagsTable
import org.simpleinvoice.server.resources.model.ProductsResponse
import java.util.UUID

class ProductRepository(
    private val tagRepository: TagRepository,
    val eventPublisher: EventPublisher,
) : ProductRepositoryInterface {
    override suspend fun all(activeOnly: Boolean): ProductsResponse =
        suspendTransaction {
            ProductsResponse(
                products =
                    if (activeOnly) {
                        ProductDAO.find { ProductTable.inactive eq false }.map { it.toProduct() }
                    } else {
                        ProductDAO.all().map { it.toProduct() }
                    },
            )
        }

    override suspend fun byIds(ids: List<UUID>): ProductsResponse =
        suspendTransaction {
            ProductsResponse(
                products = ProductDAO.find { ProductTable.id inList ids }.map { it.toProduct() },
            )
        }

    // TODO: Return Product
    override suspend fun upsert(
        product: Product,
        new: Boolean,
    ): UpsertStatement<Long> {
        val response =
            suspendTransaction {
                // Delete all tags for the invoice, since we don't know if any have been removed
                ProductTagsTable.deleteWhere { productId eq product.id }
                val upsert = upsertWithoutTransaction(product)
                product.tags.forEach { tag ->
                    tagRepository.upsertWithoutTransaction(tag = tag)
                }
                ProductTagsTable.batchUpsert(
                    data = product.tags,
                    body = { tag: Tag ->
                        this[ProductTagsTable.productId] = product.id
                        this[ProductTagsTable.tagId] = tag.id
                    },
                )
                upsert
            }
        eventPublisher.publishEvent(
            id = product.id,
            item = product,
            message = if (new) "Product created" else "Product updated",
        )
        return response
    }

    // TODO: Return Product
    override fun upsertWithoutTransaction(product: Product): UpsertStatement<Long> =
        ProductTable.upsert {
            it[id] = product.id
            it[productCode] = product.code
            it[productName] = product.name
            it[quantity] = product.quantity
            it[price] = product.price
            it[currency] = product.currency.name
            it[inactive] = product.inactive
        }

    override suspend fun delete(id: UUID): Boolean {
        val response =
            suspendTransaction {
                ProductTagsTable.deleteWhere { productId eq id }
                val rowsDeleted =
                    ProductTable.deleteWhere {
                        ProductTable.id eq id
                    }
                rowsDeleted == 1
            }
        eventPublisher.publishIdEvent(id = id, message = "Product deleted")
        return response
    }
}
