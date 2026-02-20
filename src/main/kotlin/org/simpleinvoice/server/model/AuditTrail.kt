package org.simpleinvoice.server.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.InstantSerializer
import org.simpleinvoice.server.common.UUIDSerializer
import java.time.Instant
import java.util.UUID

@Serializable
data class AuditTrail(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @Serializable(with = InstantSerializer::class) val timestamp: Instant,
    @SerialName("item_id") @Serializable(with = UUIDSerializer::class) val itemId: UUID,
    val item: String? = null,
    val message: String? = null,
    @SerialName("user_id") @Serializable(with = UUIDSerializer::class) val userId: UUID? = null,
)
