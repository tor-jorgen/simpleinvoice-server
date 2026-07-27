package org.simpleinvoice.server.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import java.time.Instant
import java.util.UUID

class InvoiceTest {
    @Test
    fun `invoice can be created`() {
        assertThat(createInvoice()).isNotNull
    }

    @Test
    fun `A valid staus transition can be made`() {
        val invoice = createInvoice().copy(status = InvoiceStatus.CANCELLED)

        assertThat(invoice.copy(status = InvoiceStatus.CANCELLED)).isNotNull
    }

    @Test
    fun `An exception is thrown if an invalid staus transition is attempted`() {
        val invoice = createInvoice().copy(status = InvoiceStatus.CANCELLED)

        assertThatThrownBy {
            invoice.copy(status = InvoiceStatus.CREATED)
        }.isInstanceOf(IllegalStateException::class.java)
    }

    private fun createInvoice(): Invoice {
        val householdId = UUID.randomUUID()
        val productId = UUID.randomUUID()
        val household =
            Household(
                id = householdId,
                name = "name",
                address = "address",
                address2 = null,
                zipCode = "zipCode",
                city = "city",
                country = "country",
                persons =
                    listOf(
                        Person(
                            id = UUID.randomUUID(),
                            firstName = "firstName",
                            lastName = "lastName",
                            emailAddress = "emailAddress",
                            phoneNumber = "phoneNumber",
                        ),
                    ),
                tags =
                    listOf(
                        Tag(
                            id = UUID.randomUUID(),
                            name = "name",
                            inactive = false,
                        ),
                    ),
                inactive = false,
            )
        val product =
            Product(
                id = productId,
                name = "Test Product",
                price = 100.0,
                tax = 25.0,
                code = "code",
                quantity = 1,
                currency = Currency.NOK,
                taxPercentage = 25.0,
                totalPrice = 125.0,
                tags =
                    listOf(
                        Tag(
                            id = UUID.randomUUID(),
                            name = "name",
                            inactive = false,
                        ),
                    ),
                inactive = false,
            )
        val invoice =
            Invoice(
                id = UUID.randomUUID(),
                invoiceNumber = 1,
                status = InvoiceStatus.CREATED,
                generatedDate = Instant.now(),
                dueDate = Instant.now().plusSeconds(86400),
                finalizedDate = null,
                price = 0.0,
                tax = 0.0,
                totalPrice = 0.0,
                currency = Currency.NOK,
                household = household,
                invoiceFilePath = null,
                tags =
                    listOf(
                        Tag(
                            id = UUID.randomUUID(),
                            name = "name",
                            inactive = false,
                        ),
                    ),
                invoiceLines =
                    listOf(
                        InvoiceLine(
                            id = UUID.randomUUID(),
                            lineNumber = 0,
                            product = product,
                            quantity = 5,
                            price = 0.0,
                            tax = 0.0,
                            totalPrice = 0.0,
                            currency = Currency.NOK,
                        ),
                        InvoiceLine(
                            id = UUID.randomUUID(),
                            lineNumber = 0,
                            product = product,
                            quantity = 2,
                            price = 0.0,
                            tax = 0.0,
                            totalPrice = 0.0,
                            currency = Currency.NOK,
                        ),
                    ),
            )

        return invoice
    }
}
