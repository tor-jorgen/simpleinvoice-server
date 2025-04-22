package org.simpleinvoice.server.resources.model

import kotlinx.serialization.Serializable
import org.simpleinvoice.server.model.Product

@Serializable
data class ProductsResponse(
    val products: List<Product>,
)
