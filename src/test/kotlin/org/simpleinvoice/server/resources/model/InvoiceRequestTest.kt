package org.simpleinvoice.server.resources.model

import org.assertj.core.api.Assertions
import org.junit.Test
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceLine
import org.simpleinvoice.server.model.InvoiceStatus
import org.simpleinvoice.server.model.Product
import org.simpleinvoice.server.model.Tag
import java.time.Instant
import java.util.UUID

class InvoiceRequestTest {
    @Test
    fun `InvoiceRequest can be converted to Invoice`() {
        val productId1 = UUID.randomUUID()
        val productId2 = UUID.randomUUID()
        val product1 =
            Product(
                id = productId1,
                code = "A",
                name = "Product A",
                quantity = 1,
                price = 123.45,
                currency = Currency.NOK,
                taxPercentage = 25.0,
                tax = 30.8625,
                totalPrice = 154.3125,
                tags = emptyList(),
                inactive = false,
            )
        val product2 =
            Product(
                id = productId2,
                code = "B",
                name = "Product B",
                quantity = 10,
                price = 56.78,
                currency = Currency.NOK,
                taxPercentage = 25.0,
                tax = 14.195,
                totalPrice = 70.975,
                tags = emptyList(),
                inactive = false,
            )
        val products =
            mapOf(
                productId1 to product1,
                productId2 to product2,
            )
        val householdId = UUID.randomUUID()
        val household =
            Household(
                id = householdId,
                name = "Household",
                address = "address",
                zipCode = "zipCode",
                city = "city",
                persons = listOf(),
                tags = listOf(),
                inactive = false,
            )
        val invoiceId = UUID.randomUUID()
        val generatedDate = Instant.now()
        val dueDate = Instant.now()
        val finalizedDate = Instant.now()
        val lineId1 = UUID.randomUUID()
        val lineId2 = UUID.randomUUID()
        Assertions
            .assertThat(
                InvoiceRequest(
                    status = InvoiceStatus.CREATED,
                    generatedDate = generatedDate,
                    dueDate = dueDate,
                    finalizedDate = finalizedDate,
                    householdId = householdId,
                    invoiceLines =
                        listOf(
                            InvoiceLineRequest(
                                id = lineId1,
                                lineNumber = 0,
                                productId = productId1,
                                quantity = 1,
                                currency = Currency.NOK,
                            ),
                            InvoiceLineRequest(
                                id = lineId2,
                                lineNumber = 1,
                                productId = productId2,
                                quantity = 2,
                                currency = Currency.NOK,
                            ),
                        ),
                    currency = Currency.NOK,
                    tags =
                        listOf(
                            TagRequestResponse(
                                id = UUID.fromString("c9029ae0-47a8-4df5-840f-81a5abbf8be5"),
                                name = "Tag A",
                                inactive = false,
                            ),
                        ),
                ).toInvoice(id = invoiceId, invoiceNumber = 1, household = household, products = products),
            ).isEqualTo(
                Invoice(
                    id = invoiceId,
                    invoiceNumber = 1,
                    status = InvoiceStatus.CREATED,
                    generatedDate = generatedDate,
                    dueDate = dueDate,
                    finalizedDate = finalizedDate,
                    price = 237.01,
                    tax = 59.2525,
                    totalPrice = 296.2625,
                    currency = Currency.NOK,
                    household = household,
                    invoiceFilePath = null,
                    invoiceLines =
                        listOf(
                            InvoiceLine(
                                id = lineId1,
                                lineNumber = 0,
                                product = product1,
                                quantity = 1,
                                price = 123.45,
                                tax = 30.8625,
                                totalPrice = 154.3125,
                                currency = Currency.NOK,
                            ),
                            InvoiceLine(
                                id = lineId2,
                                lineNumber = 1,
                                product = product2,
                                quantity = 2,
                                price = 113.56,
                                tax = 28.39,
                                totalPrice = 141.95,
                                currency = Currency.NOK,
                            ),
                        ),
                    tags =
                        listOf(
                            Tag(
                                id = UUID.fromString("c9029ae0-47a8-4df5-840f-81a5abbf8be5"),
                                name = "Tag A",
                                inactive = false,
                            ),
                        ),
                ),
            )
    }
}
