package com.redstore.data

data class Product(
    val id: Int = 0,
    val title: String = "",
    val brand: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val description: String = "",
    val images: List<String> = emptyList(),
    val sizes: List<String> = emptyList(),
    val featured: Boolean = false,
    val latest: Boolean = false
)

data class Category(
    val name: String = "",
    val image: String = ""
)

data class Testimonial(
    val name: String = "",
    val quote: String = "",
    val image: String = "",
    val rating: Double = 0.0
)

data class HomeResponse(
    val heroImage: String = "",
    val offerTitle: String = "",
    val offerText: String = "",
    val offerImage: String = "",
    val offerProductId: Int = 0,
    val categories: List<Category> = emptyList(),
    val featured: List<Product> = emptyList(),
    val latest: List<Product> = emptyList(),
    val testimonials: List<Testimonial> = emptyList(),
    val brands: List<String> = emptyList()
)

data class UserDto(
    val id: String = "",
    val name: String = "",
    val email: String = ""
)

data class AuthResponse(
    val token: String = "",
    val user: UserDto = UserDto()
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AddCartRequest(
    val productId: Int,
    val size: String,
    val quantity: Int
)

data class UpdateQtyRequest(
    val quantity: Int
)

data class CartLine(
    val id: String = "",
    val productId: Int = 0,
    val title: String = "",
    val brand: String = "",
    val image: String = "",
    val size: String = "",
    val quantity: Int = 0,
    val unitPrice: Double = 0.0,
    val lineTotal: Double = 0.0
)

data class CartResponse(
    val items: List<CartLine> = emptyList(),
    val subtotal: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val count: Int = 0
)

data class OrderRequest(
    val fullName: String,
    val phone: String,
    val address: String,
    val paymentMethod: String
)

data class Order(
    val id: String = "",
    val items: List<CartLine> = emptyList(),
    val subtotal: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val fullName: String = "",
    val phone: String = "",
    val address: String = "",
    val paymentMethod: String = "",
    val createdAt: String = ""
)

data class ApiError(val error: String = "")
