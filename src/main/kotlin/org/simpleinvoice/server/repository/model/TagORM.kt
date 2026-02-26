package org.simpleinvoice.server.repository.model

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.simpleinvoice.server.model.Tag
import java.util.UUID

object TagTable : UUIDTable("tag") {
    val name = varchar("name", 255)
    val inactive = bool("inactive")
}

class TagDAO(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TagDAO>(TagTable)

    var name by TagTable.name
    var inactive by TagTable.inactive

    fun toTag(): Tag =
        Tag(
            id = id.value,
            name = name,
            inactive = inactive,
        )
}
