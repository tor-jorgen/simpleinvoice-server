package org.simpleinvoice.server.config

import io.ktor.server.application.Application
import io.ktor.server.application.log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.simpleinvoice.server.model.AuditTrail
import org.simpleinvoice.server.repository.AuditTrailRepository
import kotlin.time.Duration.Companion.seconds
import org.koin.ktor.ext.get as getK

fun Application.handleEvents(
    channel: Channel<AuditTrail> = getK<Channel<AuditTrail>>(),
    repository: AuditTrailRepository = getK<AuditTrailRepository>(),
) {
    // Launch a coroutine in the application lifecycle
    launch {
        while (isActive) {
            try {
                for (auditTrail in channel) {
                    log.info("Received event: $auditTrail")
                    repository.add(auditTrail)
                }
            } catch (e: Exception) {
                log.error("Could not handle event", e)
            }
            delay(10.seconds)
        }
    }
}
