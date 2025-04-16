package com.example.org.simpleinvoice.repository

import com.example.org.simpleinvoice.repository.model.UserDAO
import com.example.org.simpleinvoice.repository.model.UserTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.simpleinvoice.model.User
import org.simpleinvoice.repository.suspendTransaction
import java.util.UUID

class UserRepository : UserRepositoryInterface {
    override suspend fun all(): List<User> =
        suspendTransaction {
            UserDAO.all().map { it.toUser() }
        }

    override suspend fun add(user: User): Unit =
        suspendTransaction {
            UserDAO.new {
                principalId = user.principalId
                loginProvider = user.loginProvider.name
                firstName = user.firstName
                lastName = user.lastName
                emailAddress = user.emailAddress
                scopes = user.scopes.joinToString(",")
            }
        }

    override suspend fun update(user: User): Unit =
        suspendTransaction {
            UserDAO.findByIdAndUpdate(id = user.id) {
                it.principalId = user.principalId
                it.loginProvider = user.loginProvider.name
                it.firstName = user.firstName
                it.lastName = user.lastName
                it.emailAddress = user.emailAddress
                it.scopes = user.scopes.joinToString(",")
            }
        }

    override suspend fun delete(id: UUID): Boolean =
        suspendTransaction {
            val rowsDeleted =
                UserTable.deleteWhere {
                    UserTable.id eq id
                }
            rowsDeleted == 1
        }
}
