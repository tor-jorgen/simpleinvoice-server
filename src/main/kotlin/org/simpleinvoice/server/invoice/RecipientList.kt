package org.simpleinvoice.server.invoice

import org.simpleinvoice.server.model.Household

data class RecipientList(
    private val recipients: List<Recipient>,
) {
    companion object {
        fun fromHouseHold(household: Household): List<Recipient> =
            household.persons.map { person ->
                Recipient(
                    name = "${person.firstName} ${person.lastName}",
                    addressLine1 = household.address,
                    addressLine2 = "${household.zipCode} ${household.city}",
                    // TODO: Allow null
                    addressLine3 = household.country ?: "",
                    // TODO: Allow null
                    email = person.emailAddress ?: "",
                )
            }
    }
}
