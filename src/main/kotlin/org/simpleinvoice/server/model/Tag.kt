package org.simpleinvoice.server.model

import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import java.util.UUID

@Serializable
data class Tag(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val name: String,
    val inactive: Boolean = false,
)
