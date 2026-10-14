package com.redstore

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.apiRoutes() {
    route("/api") {
        get("/health") {
            call.respond(Health("ok", "redstore"))
        }

        get("/home") {
            call.respond(homePayload())
        }

        get("/products") {
            val category = call.request.queryParameters["category"]
            val q = call.request.queryParameters["q"]?.trim()?.lowercase().orEmpty()
            val result = catalog.filter { product ->
                (category.isNullOrBlank() || product.category.equals(category, ignoreCase = true)) &&
                    (q.isEmpty() || product.title.lowercase().contains(q) || product.brand.lowercase().contains(q))
            }
            call.respond(result)
        }

        get("/products/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw ApiException(HttpStatusCode.BadRequest, "Invalid product id")
            call.respond(findProduct(id))
        }

        get("/categories") {
            call.respond(categories)
        }

        post("/auth/register") {
            val body = call.receive<RegisterRequest>()
            call.respond(HttpStatusCode.Created, Store.register(body.name, body.email, body.password))
        }

        post("/auth/login") {
            val body = call.receive<LoginRequest>()
            call.respond(Store.login(body.email, body.password))
        }

        get("/me") {
            call.respond(call.requireUser().toDto())
        }

        get("/cart") {
            call.respond(Store.cart(call.requireUser()))
        }

        post("/cart") {
            val body = call.receive<AddCartRequest>()
            call.respond(Store.addToCart(call.requireUser(), body.productId, body.size, body.quantity))
        }

        put("/cart/{id}") {
            val body = call.receive<UpdateQtyRequest>()
            val id = call.parameters["id"] ?: throw ApiException(HttpStatusCode.BadRequest, "Missing line id")
            call.respond(Store.updateQuantity(call.requireUser(), id, body.quantity))
        }

        delete("/cart/{id}") {
            val id = call.parameters["id"] ?: throw ApiException(HttpStatusCode.BadRequest, "Missing line id")
            call.respond(Store.removeFromCart(call.requireUser(), id))
        }

        get("/wishlist") {
            call.respond(Store.wishlist(call.requireUser()))
        }

        post("/wishlist/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw ApiException(HttpStatusCode.BadRequest, "Invalid product id")
            call.respond(Store.addWishlist(call.requireUser(), id))
        }

        delete("/wishlist/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw ApiException(HttpStatusCode.BadRequest, "Invalid product id")
            call.respond(Store.removeWishlist(call.requireUser(), id))
        }

        get("/orders") {
            call.respond(Store.orders(call.requireUser()))
        }

        post("/orders") {
            val body = call.receive<OrderRequest>()
            call.respond(HttpStatusCode.Created, Store.placeOrder(call.requireUser(), body))
        }
    }
}

private fun ApplicationCall.requireUser(): UserRecord {
    val header = request.headers["Authorization"].orEmpty()
    val token = header.removePrefix("Bearer").trim()
    if (token.isEmpty()) throw ApiException(HttpStatusCode.Unauthorized, "Sign in to continue")
    return Store.userForToken(token) ?: throw ApiException(HttpStatusCode.Unauthorized, "Session expired. Sign in again")
}
