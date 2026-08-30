package org.simpleinvoice.server.invoice

import kotlinx.coroutines.channels.Channel
import org.simpleinvoice.server.model.AuditTrail
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tools.jackson.databind.ObjectMapper
import java.time.Instant
import java.util.UUID

class EventPublisher(
    private val channel: Channel<AuditTrail>,
) {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper = ObjectMapper()

    suspend fun publishEvent(
        id: UUID,
        item: Any,
        message: String,
        userMessage: String? = null,
    ) {
        try {
            val auditTrail =
                create(id = id, item = mapper.writeValueAsString(item), message = message, userMessage = userMessage)
            channel.send(auditTrail)
        } catch (e: Exception) {
            logger.error("Failed to publish event", e)
        }
    }

    suspend fun publishIdEvent(
        id: UUID,
        message: String,
        userMessage: String? = null,
    ) {
        try {
            val auditTrail = create(id = id, item = "", message = message, userMessage = userMessage)
            channel.send(auditTrail)
        } catch (e: Exception) {
            logger.error("Failed to publish ID event", e)
        }
    }

    private fun create(
        id: UUID,
        item: String?,
        message: String? = null,
        userMessage: String? = null,
    ): AuditTrail =
        AuditTrail(
            id = UUID.randomUUID(),
            timestamp = Instant.now(),
            itemId = id,
            item = item,
            message = message,
            userMessage = userMessage,
        )
}
