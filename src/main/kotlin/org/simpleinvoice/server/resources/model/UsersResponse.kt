package org.simpleinvoice.server.resources.model

import kotlinx.serialization.Serializable
import org.simpleinvoice.server.model.User

@Serializable
data class UsersResponse(
    val users: List<User>,
)
