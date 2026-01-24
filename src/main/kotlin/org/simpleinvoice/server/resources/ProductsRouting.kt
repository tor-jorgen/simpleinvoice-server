@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package org.simpleinvoice.server.resources

import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.simpleinvoice.server.common.UUIDSerializer
import org.simpleinvoice.server.repository.ProductRepository
import org.simpleinvoice.server.resources.model.ProductRequest
import org.simpleinvoice.server.resources.model.ProductResponse
import org.simpleinvoice.server.resources.model.ProductsResponse
import java.util.UUID
import org.koin.ktor.ext.get as getK

@Resource("/products")
class Products(
    @SerialName("active_only") val activeOnly: Boolean = false,
) {
    @Resource("{id}")
    class Id(
        @Suppress("unused") val parent: Products = Products(),
        @Serializable(with = UUIDSerializer::class) val id: UUID,
    )
}

/**
 * These routes require a valid session, otherwise you have to log in
 */
fun Application.configureProductsRouting(repository: ProductRepository = getK<ProductRepository>()) {
    routing {
//        authenticate(AUTH_SESSION) {
        get<Products> {
            // Get all products
            val activeOnly = (call.queryParameters["active_only"] ?: "false").toBoolean()
            val response =
                ProductsResponse(products = repository.all(activeOnly = activeOnly).map { ProductResponse.fromProduct(it) })
            call.respond(status = HttpStatusCode.OK, message = response)
        }

        post<Products> {
            // Create a new product
            val productRequest = call.receive<ProductRequest>()
            val product = productRequest.toProduct(UUID.randomUUID())
            repository.upsert(product = product, new = true)
            call.respond(status = HttpStatusCode.Created, message = product)
        }

        put<Products.Id> { request ->
            // Update a product
            val productRequest = call.receive<ProductRequest>()
            val product = productRequest.toProduct(request.id)
            val response = ProductResponse.fromProduct(repository.upsert(product = product, new = false))
            call.respond(status = HttpStatusCode.OK, message = response)
        }

        delete<Products.Id> { request ->
            // Delete a product
            repository.delete(request.id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
//    }
}
