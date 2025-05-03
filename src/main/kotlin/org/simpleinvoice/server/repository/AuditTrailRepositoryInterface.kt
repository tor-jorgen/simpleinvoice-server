package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.AuditTrail
import java.util.UUID

interface AuditTrailRepositoryInterface {
    suspend fun all(id: UUID): List<AuditTrail>

    suspend fun add(auditTrail: AuditTrail): Unit
}
