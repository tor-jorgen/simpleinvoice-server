package org.simpleinvoice.server.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.upsert
import org.simpleinvoice.server.invoice.EventPublisher
import org.simpleinvoice.server.model.LoginProvider
import org.simpleinvoice.server.model.User
import org.simpleinvoice.server.repository.model.UserDAO
import org.simpleinvoice.server.repository.model.UserTable
import org.simpleinvoice.server.resources.model.UsersResponse
import java.util.UUID

class UserRepository(
    val eventPublisher: EventPublisher,
) : UserRepositoryInterface {
    override suspend fun all(): UsersResponse =
        suspendTransaction {
            UsersResponse(
                users = UserDAO.all().map { it.toUser() },
            )
        }

    override suspend fun upsert(
        user: User,
        new: Boolean,
    ): User {
        val response =
            suspendTransaction {
                upsertWithoutTransaction(user)
            }
        eventPublisher.publishEvent(
            id = user.id,
            item = user,
            message = if (new) "User created" else "User updated",
        )
        return response
    }

    override fun upsertWithoutTransaction(user: User): User =
        toUser(
            UserTable.upsert {
                it[id] = user.id
                it[principalId] = user.principalId
                it[loginProvider] = user.loginProvider.name
                it[firstName] = user.firstName
                it[lastName] = user.lastName
                it[emailAddress] = user.emailAddress
                it[emailAddress] = user.emailAddress
                it[scopes] = user.scopes.joinToString(",")
            },
        )

    override suspend fun delete(id: UUID): Boolean {
        val response =
            suspendTransaction {
                val rowsDeleted =
                    UserTable.deleteWhere {
                        UserTable.id eq id
                    }
                rowsDeleted == 1
            }
        eventPublisher.publishIdEvent(id = id, message = "User deleted")
        return response
    }

    private fun toUser(result: UpsertStatement<Long>): User =
        User(
            id = result[UserTable.id].value,
            principalId = result[UserTable.principalId],
            loginProvider = LoginProvider.valueOf(result[UserTable.loginProvider]),
            firstName = result[UserTable.firstName],
            lastName = result[UserTable.lastName],
            emailAddress = result[UserTable.emailAddress],
            scopes = result[UserTable.scopes].split(",").toSet(),
            inactive = result[UserTable.inactive],
        )
}
