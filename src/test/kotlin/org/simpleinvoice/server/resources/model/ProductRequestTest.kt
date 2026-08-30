package org.simpleinvoice.server.resources.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.simpleinvoice.server.model.Currency
import org.simpleinvoice.server.model.Product
import org.simpleinvoice.server.model.Tag
import java.util.UUID

class ProductRequestTest {
    @Test
    fun `ProductRequest can be converted to Product`() {
        assertThat(
            ProductRequest(
                item =
                    ProductRequestObject(
                        code = "code",
                        name = "name",
                        quantity = 100,
                        price = "567",
                        currency = Currency.NOK,
                        taxPercentage = "25.0",
                        tags =
                            listOf(
                                TagDTO(
                                    id = UUID.fromString("c9029ae0-47a8-4df5-840f-81a5abbf8be5"),
                                    name = "Tag A",
                                    inactive = false,
                                ),
                            ),
                        inactive = false,
                    ),
                message = "message",
            ).toProduct(id = UUID.fromString("737500f2-d438-4428-a739-270d901bb4f4")),
        ).isEqualTo(
            Product(
                id = UUID.fromString("737500f2-d438-4428-a739-270d901bb4f4"),
                code = "code",
                name = "name",
                quantity = 100,
                price = 567.0,
                currency = Currency.NOK,
                taxPercentage = 25.0,
                tax = 141.75,
                totalPrice = 708.75,
                tags =
                    listOf(
                        Tag(
                            id = UUID.fromString("c9029ae0-47a8-4df5-840f-81a5abbf8be5"),
                            name = "Tag A",
                            inactive = false,
                        ),
                    ),
                inactive = false,
            ),
        )
    }
}
