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
                    addressLine2 = household.address2,
                    zipCity = "${household.zipCode} ${household.city}",
                    country = household.country,
                    email = person.emailAddress,
                )
            }
    }
}
