package org.simpleinvoice.server.resources.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.model.LoginProvider
import org.simpleinvoice.server.model.User
import java.util.UUID

@Serializable
data class UserResponse(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    @SerialName("principal_id") val principalId: String,
    @SerialName("login_provider") val loginProvider: LoginProvider,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email_address") val emailAddress: String,
    val scopes: Set<String>,
    val inactive: Boolean,
) {
    companion object {
        fun fromUser(user: User) =
            UserResponse(
                id = user.id,
                principalId = user.principalId,
                loginProvider = user.loginProvider,
                firstName = user.firstName,
                lastName = user.lastName,
                emailAddress = user.emailAddress,
                scopes = user.scopes,
                inactive = user.inactive,
            )
    }
}
