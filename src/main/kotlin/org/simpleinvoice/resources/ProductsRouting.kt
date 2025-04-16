package com.example.org.simpleinvoice.resources

import com.example.org.simpleinvoice.repository.ProductRepository
import com.example.org.simpleinvoice.resources.model.ProductRequest
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
import kotlinx.serialization.Serializable
import org.simpleinvoice.common.UUIDSerializer
import java.util.UUID

@Resource("/products")
class Products {
    @Resource("{id}")
    class Id(
        val parent: Products = Products(),
        @Serializable(with = UUIDSerializer::class) val id: UUID,
    )
}

/**
 * These routes require a valid session, otherwise you have to log in
 */
fun Application.configureProductsRouting() {
    val repository = ProductRepository() // TODO: Inject

    routing {
//        authenticate(AUTH_SESSION) {
        get<Products> {
            // Get all products
            call.respond(status = HttpStatusCode.OK, message = repository.all())
        }

        post<Products> {
            // Create a new product
            val productRequest = call.receive<ProductRequest>()
            val product = productRequest.toProduct(UUID.randomUUID())
            repository.upsert(product)
            call.respond(status = HttpStatusCode.Created, message = product)
        }

        //        get<Household.Id> { request ->
//            // Show a customer with id ${customer.id}
//            call.respondText("An article with id ${request.id} is fetched", status = HttpStatusCode.OK)
//        }

        put<Products.Id> { request ->
            // Update a product
            val productRequest = call.receive<ProductRequest>()
            val product = productRequest.toProduct(request.id)
            repository.upsert(product)
            call.respond(status = HttpStatusCode.OK, message = product)
        }

        delete<Products.Id> { request ->
            // Delete a product
            repository.delete(request.id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
//    }
}
