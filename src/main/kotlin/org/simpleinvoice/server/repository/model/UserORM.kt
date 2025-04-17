package org.simpleinvoice.server.repository.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.simpleinvoice.server.model.LoginProvider
import org.simpleinvoice.server.model.User
import java.util.UUID

object UserTable : UUIDTable("application_user") {
    val principalId = varchar("principal_id", 255)
    val loginProvider = varchar("login_provider", 255)
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val emailAddress = varchar("email_address", 255)
    val scopes = varchar("scopes", 1024)
}

class UserDAO(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserDAO>(UserTable)

    var principalId by UserTable.principalId
    var loginProvider by UserTable.loginProvider
    var firstName by UserTable.firstName
    var lastName by UserTable.lastName
    var emailAddress by UserTable.emailAddress
    var scopes by UserTable.scopes

    fun toUser(): User =
        User(
            id = id.value,
            principalId = principalId,
            loginProvider = LoginProvider.valueOf(loginProvider),
            firstName = firstName,
            lastName = lastName,
            emailAddress = emailAddress,
            scopes = scopes.split(",").toSet(),
        )
}
