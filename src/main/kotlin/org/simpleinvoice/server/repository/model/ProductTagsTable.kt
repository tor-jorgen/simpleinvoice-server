package org.simpleinvoice.server.repository.model

import org.jetbrains.exposed.sql.Table

object ProductTagsTable : Table("product_tags") {
    val productId = reference("product_id", HouseholdTable)
    val tagId = reference("tag_id", TagTable)
    override val primaryKey = PrimaryKey(productId, tagId)
}
