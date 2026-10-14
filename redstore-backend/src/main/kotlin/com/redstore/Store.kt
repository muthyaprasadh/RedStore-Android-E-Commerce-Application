package com.redstore

import io.ktor.http.HttpStatusCode
import java.security.MessageDigest
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object Store {
    private val usersByEmail = ConcurrentHashMap<String, UserRecord>()
    private val tokens = ConcurrentHashMap<String, String>()
    private val carts = ConcurrentHashMap<String, MutableList<StoredCartItem>>()
    private val wishlists = ConcurrentHashMap<String, MutableSet<Int>>()
    private val orders = ConcurrentHashMap<String, MutableList<Order>>()

    init {
        val demo = createUser("Demo Shopper", "demo@redstore.com", "password123")
        usersByEmail[demo.email] = demo
    }

    fun register(name: String, email: String, password: String): AuthResponse {
        val cleanEmail = email.trim().lowercase()
        val cleanName = name.trim()
        if (cleanName.length < 2) throw ApiException(HttpStatusCode.BadRequest, "Enter your name")
        if (!cleanEmail.contains("@") || cleanEmail.length < 5) {
            throw ApiException(HttpStatusCode.BadRequest, "Enter a valid email")
        }
        if (password.length < 4) throw ApiException(HttpStatusCode.BadRequest, "Password must be at least 4 characters")
        if (usersByEmail.containsKey(cleanEmail)) {
            throw ApiException(HttpStatusCode.Conflict, "An account with that email already exists")
        }
        val user = createUser(cleanName, cleanEmail, password)
        usersByEmail[cleanEmail] = user
        return issue(user)
    }

    fun login(email: String, password: String): AuthResponse {
        val user = usersByEmail[email.trim().lowercase()]
            ?: throw ApiException(HttpStatusCode.Unauthorized, "Email or password is incorrect")
        if (user.passwordHash != hash(password)) {
            throw ApiException(HttpStatusCode.Unauthorized, "Email or password is incorrect")
        }
        return issue(user)
    }

    fun userForToken(token: String): UserRecord? {
        val email = tokens[token] ?: return null
        return usersByEmail[email]
    }

    fun cart(user: UserRecord): CartResponse = synchronized(carts) {
        toCart(carts.getOrPut(user.id) { mutableListOf() })
    }

    fun addToCart(user: UserRecord, productId: Int, size: String, quantity: Int): CartResponse {
        val product = findProduct(productId)
        val chosen = size.ifBlank { product.sizes.first() }
        if (chosen !in product.sizes) throw ApiException(HttpStatusCode.BadRequest, "That size is not available")
        val qty = quantity.coerceAtLeast(1)
        synchronized(carts) {
            val items = carts.getOrPut(user.id) { mutableListOf() }
            val existing = items.find { it.productId == productId && it.size == chosen }
            if (existing == null) items.add(StoredCartItem(productId, chosen, qty))
            else existing.quantity += qty
            return toCart(items)
        }
    }

    fun updateQuantity(user: UserRecord, lineId: String, quantity: Int): CartResponse {
        if (quantity < 1) throw ApiException(HttpStatusCode.BadRequest, "Quantity must be at least 1")
        synchronized(carts) {
            val items = carts.getOrPut(user.id) { mutableListOf() }
            val item = items.find { lineIdOf(it) == lineId }
                ?: throw ApiException(HttpStatusCode.NotFound, "Item is not in your bag")
            item.quantity = quantity
            return toCart(items)
        }
    }

    fun removeFromCart(user: UserRecord, lineId: String): CartResponse = synchronized(carts) {
        val items = carts.getOrPut(user.id) { mutableListOf() }
        items.removeIf { lineIdOf(it) == lineId }
        toCart(items)
    }

    fun wishlist(user: UserRecord): List<Product> {
        val ids = wishlists.getOrPut(user.id) { mutableSetOf() }
        return catalog.filter { it.id in ids }
    }

    fun addWishlist(user: UserRecord, productId: Int): List<Product> {
        findProduct(productId)
        wishlists.getOrPut(user.id) { mutableSetOf() }.add(productId)
        return wishlist(user)
    }

    fun removeWishlist(user: UserRecord, productId: Int): List<Product> {
        wishlists.getOrPut(user.id) { mutableSetOf() }.remove(productId)
        return wishlist(user)
    }

    fun placeOrder(user: UserRecord, request: OrderRequest): Order {
        val name = request.fullName.trim()
        val phone = request.phone.trim()
        val address = request.address.trim()
        if (name.length < 2) throw ApiException(HttpStatusCode.BadRequest, "Enter the recipient name")
        if (phone.length < 6) throw ApiException(HttpStatusCode.BadRequest, "Enter a phone number")
        if (address.length < 8) throw ApiException(HttpStatusCode.BadRequest, "Enter a delivery address")
        val method = request.paymentMethod.ifBlank { "Cash on delivery" }
        synchronized(carts) {
            val items = carts.getOrPut(user.id) { mutableListOf() }
            val snapshot = toCart(items)
            if (snapshot.items.isEmpty()) throw ApiException(HttpStatusCode.BadRequest, "Your bag is empty")
            val order = Order(
                id = "RS-" + UUID.randomUUID().toString().substring(0, 8).uppercase(),
                items = snapshot.items,
                subtotal = snapshot.subtotal,
                shipping = snapshot.shipping,
                total = snapshot.total,
                fullName = name,
                phone = phone,
                address = address,
                paymentMethod = method,
                createdAt = Instant.now().toString()
            )
            orders.getOrPut(user.id) { mutableListOf() }.add(0, order)
            items.clear()
            return order
        }
    }

    fun orders(user: UserRecord): List<Order> = orders[user.id]?.toList() ?: emptyList()

    private fun issue(user: UserRecord): AuthResponse {
        val token = UUID.randomUUID().toString()
        tokens[token] = user.email
        return AuthResponse(token, user.toDto())
    }

    private fun createUser(name: String, email: String, password: String) = UserRecord(
        id = UUID.randomUUID().toString(),
        name = name,
        email = email,
        passwordHash = hash(password)
    )

    private fun hash(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest("redstore::$password".toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun lineIdOf(item: StoredCartItem) = "${item.productId}:${item.size}"

    private fun toCart(items: List<StoredCartItem>): CartResponse {
        val lines = items.map { item ->
            val product = findProduct(item.productId)
            val line = round2(product.price * item.quantity)
            CartLine(
                id = lineIdOf(item),
                productId = product.id,
                title = product.title,
                brand = product.brand,
                image = product.images.first(),
                size = item.size,
                quantity = item.quantity,
                unitPrice = product.price,
                lineTotal = line
            )
        }
        val subtotal = round2(lines.sumOf { it.lineTotal })
        val shipping = if (subtotal == 0.0 || subtotal >= 100.0) 0.0 else 15.0
        return CartResponse(
            items = lines,
            subtotal = subtotal,
            shipping = shipping,
            total = round2(subtotal + shipping),
            count = lines.sumOf { it.quantity }
        )
    }

    private fun round2(value: Double) = kotlin.math.round(value * 100.0) / 100.0
}
