package org.simpleinvoice.server.repository

import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

suspend fun <T> executeInTransaction(block: Transaction.() -> T): T = suspendTransaction { block() }
