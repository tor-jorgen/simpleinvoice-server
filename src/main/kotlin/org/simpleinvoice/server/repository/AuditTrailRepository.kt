package org.simpleinvoice.server.repository

import org.simpleinvoice.server.model.AuditTrail
import org.simpleinvoice.server.repository.model.AuditTrailDAO
import java.util.UUID

class AuditTrailRepository : AuditTrailRepositoryInterface {
    override suspend fun all(id: UUID): List<AuditTrail> =
        suspendTransaction {
            AuditTrailDAO.all().map { it.toAuditTrail() }
        }

    override suspend fun add(auditTrail: AuditTrail): Unit =
        suspendTransaction {
            AuditTrailDAO.new {
                timestamp = auditTrail.timestamp.toString()
                itemId = auditTrail.itemId.toString()
                item = auditTrail.item
                message = auditTrail.message
                userId = auditTrail.userId.toString()
            }
        }
}
