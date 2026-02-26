package org.simpleinvoice.server.repository.model

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.simpleinvoice.server.model.AuditTrail
import java.time.Instant
import java.util.UUID

object AuditTrailTable : UUIDTable("audit_trail") {
    val timestamp = varchar("timestamp", 30)
    val itemId = varchar("item_id", 36)
    val item = varchar("item", 2048).nullable()
    val message = varchar("message", 255).nullable()
    val userId = varchar("user_id", 36).nullable()
}

class AuditTrailDAO(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AuditTrailDAO>(AuditTrailTable)

    var timestamp by AuditTrailTable.timestamp
    var itemId by AuditTrailTable.itemId
    var item by AuditTrailTable.item
    var message by AuditTrailTable.message
    var userId by AuditTrailTable.userId

    fun toAuditTrail(): AuditTrail =
        AuditTrail(
            id = id.value,
            timestamp = Instant.parse(timestamp),
            itemId = UUID.fromString(itemId),
            item = item,
            message = message,
            userId = userId?.let { UUID.fromString(it) },
        )
}
