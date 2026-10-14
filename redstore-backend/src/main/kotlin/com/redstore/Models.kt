package com.redstore

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val title: String,
    val brand: String,
    val category: String,
    val price: Double,
    val rating: Double,
    val description: String,
    val images: List<String>,
    val sizes: List<String>,
    val featured: Boolean = false,
    val latest: Boolean = false
)

@Serializable
data class Category(
    val name: String,
    val image: String
)

@Serializable
data class Testimonial(
    val name: String,
    val quote: String,
    val image: String,
    val rating: Double
)

@Serializable
data class HomeResponse(
    val heroImage: String,
    val offerTitle: String,
    val offerText: String,
    val offerImage: String,
    val offerProductId: Int,
    val categories: List<Category>,
    val featured: List<Product>,
    val latest: List<Product>,
    val testimonials: List<Testimonial>,
    val brands: List<String>
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserDto
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class AddCartRequest(
    val productId: Int,
    val size: String,
    val quantity: Int = 1
)

@Serializable
data class UpdateQtyRequest(
    val quantity: Int
)

@Serializable
data class CartLine(
    val id: String,
    val productId: Int,
    val title: String,
    val brand: String,
    val image: String,
    val size: String,
    val quantity: Int,
    val unitPrice: Double,
    val lineTotal: Double
)

@Serializable
data class CartResponse(
    val items: List<CartLine>,
    val subtotal: Double,
    val shipping: Double,
    val total: Double,
    val count: Int
)

@Serializable
data class OrderRequest(
    val fullName: String,
    val phone: String,
    val address: String,
    val paymentMethod: String
)

@Serializable
data class Order(
    val id: String,
    val items: List<CartLine>,
    val subtotal: Double,
    val shipping: Double,
    val total: Double,
    val fullName: String,
    val phone: String,
    val address: String,
    val paymentMethod: String,
    val createdAt: String
)

@Serializable
data class ApiError(val error: String)

@Serializable
data class Health(val status: String, val service: String)

data class UserRecord(
    val id: String,
    val name: String,
    val email: String,
    val passwordHash: String
) {
    fun toDto() = UserDto(id, name, email)
}

data class StoredCartItem(
    val productId: Int,
    val size: String,
    var quantity: Int
)

class ApiException(val status: io.ktor.http.HttpStatusCode, message: String) : RuntimeException(message)
