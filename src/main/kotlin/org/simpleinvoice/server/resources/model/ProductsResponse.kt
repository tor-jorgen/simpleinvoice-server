package org.simpleinvoice.server.resources.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponse(
    val products: List<ProductResponse>,
)
