package org.simpleinvoice.server.invoice

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Household
import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.model.InvoiceLine
import org.simpleinvoice.server.model.InvoiceStatus
import org.simpleinvoice.server.model.Person
import org.simpleinvoice.server.model.Product
import org.simpleinvoice.server.model.Tag
import org.simpleinvoice.server.repository.HouseholdRepository
import org.simpleinvoice.server.repository.InvoiceRepository
import org.simpleinvoice.server.repository.ProductRepository
import java.time.Instant
import java.util.UUID

class InvoiceGeneratorTest {
    private val productRepository = mock<ProductRepository>()
    private val householdRepository = mock<HouseholdRepository>()
    private val invoiceRepository = mock<InvoiceRepository>()
    private val documentGenerator = mock<DocumentGenerator>()
    private val emailGenerator = mock<EmailGenerator>()

    private val invoiceGenerator =
        InvoiceGenerator(
            productRepository = productRepository,
            householdRepository = householdRepository,
            invoiceRepository = invoiceRepository,
            documentGenerator = documentGenerator,
            emailGenerator = emailGenerator,
        )

    @Test
    fun `should generate invoice for household`() {
        runBlocking {
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

            whenever(householdRepository.get(householdId)).thenReturn(household)
            whenever(productRepository.byIds(any())).thenReturn(listOf(product))
            whenever(invoiceRepository.upsert(any(), any())).thenReturn(invoice)
            whenever(documentGenerator.createDocuments(any())).thenReturn(
                Triple(
                    "test-invoice",
                    "/tmp/test.odt",
                    "/tmp/test.pdf",
                ),
            )

            val result =
                invoiceGenerator.generate(
                    invoice = invoice,
                    householdIds = listOf(householdId),
                    email = null,
                    new = true,
                )

            verify(householdRepository).get(householdId)
            // TODO: Use set?
            verify(productRepository).byIds(listOf(productId, productId))

            val invoiceCaptor = argumentCaptor<Invoice>()
            verify(invoiceRepository).upsert(invoiceCaptor.capture(), eq(true))

            assertThat(result).isNotNull
            val capturedInvoice = invoiceCaptor.firstValue
            assertThat(capturedInvoice.invoiceLines).hasSize(2)
            assertThat(capturedInvoice.invoiceLines[0].quantity).isEqualTo(5)
            assertThat(capturedInvoice.invoiceLines[0].price).isEqualTo(500.0)
            assertThat(capturedInvoice.invoiceLines[0].tax).isEqualTo(125.0)
            assertThat(capturedInvoice.invoiceLines[0].totalPrice).isEqualTo(625.0)
            assertThat(capturedInvoice.invoiceLines[1].quantity).isEqualTo(2)
            assertThat(capturedInvoice.invoiceLines[1].price).isEqualTo(200.0)
            assertThat(capturedInvoice.invoiceLines[1].tax).isEqualTo(50.0)
            assertThat(capturedInvoice.invoiceLines[1].totalPrice).isEqualTo(250.0)
            assertThat(capturedInvoice.price).isEqualTo(700.0)
            assertThat(capturedInvoice.tax).isEqualTo(175.0)
            assertThat(capturedInvoice.totalPrice).isEqualTo(875.0)
        }
    }
}
