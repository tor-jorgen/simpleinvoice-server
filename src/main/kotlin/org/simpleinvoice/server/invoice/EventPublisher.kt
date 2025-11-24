package org.simpleinvoice.server.invoice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import kotlinx.coroutines.channels.Channel
import org.simpleinvoice.server.model.AuditTrail
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

class EventPublisher(
    private val channel: Channel<AuditTrail>,
) {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper =
        ObjectMapper().apply {
            // Use ISO-8601 format
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

    suspend fun publishEvent(
        id: UUID,
        item: Any,
        message: String,
    ) {
        try {
            val auditTrail = create(id = id, item = mapper.writeValueAsString(item), message = message)
            channel.send(auditTrail)
        } catch (e: Exception) {
            logger.error("Failed to publish event", e)
        }
    }

    suspend fun publishIdEvent(
        id: UUID,
        message: String,
    ) {
        try {
            val auditTrail = create(id = id, item = "", message = message)
            channel.send(auditTrail)
        } catch (e: Exception) {
            logger.error("Failed to publish ID event", e)
        }
    }

    private fun create(
        id: UUID,
        item: String?,
        message: String? = null,
    ): AuditTrail =
        AuditTrail(
            id = UUID.randomUUID(),
            timestamp = Instant.now(),
            itemId = id,
            item = item,
            message = message,
        )
}
