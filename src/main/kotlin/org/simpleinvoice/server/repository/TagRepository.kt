package org.simpleinvoice.server.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.upsert
import org.simpleinvoice.server.invoice.EventPublisher
import org.simpleinvoice.server.model.Tag
import org.simpleinvoice.server.repository.model.TagDAO
import org.simpleinvoice.server.repository.model.TagTable
import org.simpleinvoice.server.resources.model.TagsResponse
import java.util.UUID

class TagRepository(
    val eventPublisher: EventPublisher,
) : TagRepositoryInterface {
    override suspend fun all(activeOnly: Boolean): TagsResponse =
        suspendTransaction {
            TagsResponse(
                tags =
                    if (activeOnly) {
                        TagDAO.find { TagTable.inactive eq false }.map { it.toTag() }
                    } else {
                        TagDAO.all().map { it.toTag() }
                    },
            )
        }

    override suspend fun upsert(
        tag: Tag,
        new: Boolean,
    ): Tag {
        val response =
            suspendTransaction {
                upsertWithoutTransaction(tag)
            }
        eventPublisher.publishEvent(
            id = tag.id,
            item = tag,
            message = if (new) "Tag created" else "Tag updated",
        )
        return response
    }

    override fun upsertWithoutTransaction(tag: Tag): Tag =
        toTag(
            TagTable.upsert {
                it[id] = tag.id
                it[name] = tag.name
                it[inactive] = tag.inactive
            },
        )

    override suspend fun delete(id: UUID): Boolean {
        val response =
            suspendTransaction {
                val rowsDeleted =
                    TagTable.deleteWhere {
                        TagTable.id eq id
                    }
                rowsDeleted == 1
            }
        eventPublisher.publishIdEvent(id = id, message = "Tag deleted")
        return response
    }

    private fun toTag(result: UpsertStatement<Long>): Tag =
        Tag(
            id = result[TagTable.id].value,
            name = result[TagTable.name],
            inactive = result[TagTable.inactive],
        )
}
