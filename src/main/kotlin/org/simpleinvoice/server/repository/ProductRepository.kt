package org.simpleinvoice.server.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.upsert
import org.simpleinvoice.server.model.Product
import org.simpleinvoice.server.repository.model.ProductDAO
import org.simpleinvoice.server.repository.model.ProductTable
import java.util.UUID

class ProductRepository : ProductRepositoryInterface {
    override suspend fun all(): List<Product> =
        suspendTransaction {
            ProductDAO.all().map { it.toProduct() }
        }

    override suspend fun upsert(product: Product): UpsertStatement<Long> =
        suspendTransaction {
            upsertWithoutTransaction(product)
        }

    override fun upsertWithoutTransaction(product: Product): UpsertStatement<Long> =
        ProductTable.upsert {
            it[productCode] = product.code
            it[productName] = product.name
            it[quantity] = product.quantity
            it[price] = product.price
            it[currency] = product.currency.name
        }

    override suspend fun delete(id: UUID): Boolean =
        suspendTransaction {
            val rowsDeleted =
                ProductTable.deleteWhere {
                    ProductTable.id eq id
                }
            rowsDeleted == 1
        }
}
