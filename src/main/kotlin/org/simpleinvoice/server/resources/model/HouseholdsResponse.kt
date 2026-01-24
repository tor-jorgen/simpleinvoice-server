package org.simpleinvoice.server.resources.model

import kotlinx.serialization.Serializable
import org.simpleinvoice.server.model.Household

@Serializable
data class HouseholdsResponse(
    val households: List<HouseholdResponse>,
)
