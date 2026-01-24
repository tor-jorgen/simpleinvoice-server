package org.simpleinvoice.server.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.upsert
import org.simpleinvoice.server.invoice.EventPublisher
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Product
import org.simpleinvoice.server.model.Tag
import org.simpleinvoice.server.repository.model.ProductDAO
import org.simpleinvoice.server.repository.model.ProductTable
import org.simpleinvoice.server.repository.model.ProductTagsTable
import java.util.UUID

class ProductRepository(
    val eventPublisher: EventPublisher,
) : ProductRepositoryInterface {
    override suspend fun all(activeOnly: Boolean): List<Product> =
        suspendTransaction {
            if (activeOnly) {
                ProductDAO.find { ProductTable.inactive eq false }.map { it.toProduct() }
            } else {
                ProductDAO.all().map { it.toProduct() }
            }
        }

    override suspend fun byIds(ids: List<UUID>): List<Product> =
        suspendTransaction {
            ProductDAO.find { ProductTable.id inList ids }.map { it.toProduct() }
        }

    override suspend fun upsert(
        product: Product,
        new: Boolean,
    ): Product {
        val response =
            suspendTransaction {
                // Delete all tags for the invoice, since we don't know if any have been removed
                ProductTagsTable.deleteWhere { productId eq product.id }
                val upsert =
                    toProduct(
                        ProductTable.upsert {
                            it[id] = product.id
                            it[productCode] = product.code
                            it[productName] = product.name
                            it[quantity] = product.quantity
                            it[price] = product.price
                            it[currency] = product.currency.name
                            it[taxPercentage] = product.taxPercentage
                            it[totalPrice] = product.totalPrice
                            it[inactive] = product.inactive
                        },
                        product.tags,
                    )
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

    private fun toProduct(
        result: UpsertStatement<Long>,
        tags: List<Tag>,
    ): Product =
        Product(
            id = result[ProductTable.id].value,
            code = result[ProductTable.productCode],
            name = result[ProductTable.productName],
            quantity = result[ProductTable.quantity],
            price = result[ProductTable.price],
            currency = Currency.valueOf(result[ProductTable.currency]),
            taxPercentage = result[ProductTable.taxPercentage],
            totalPrice = result[ProductTable.totalPrice],
            tags = tags,
            inactive = result[ProductTable.inactive],
        )
}
