package com.redstore.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.redstore.BuildConfig
import com.redstore.data.AddCartRequest
import com.redstore.data.ApiError
import com.redstore.data.ApiFactory
import com.redstore.data.CartResponse
import com.redstore.data.HomeResponse
import com.redstore.data.LoginRequest
import com.redstore.data.Order
import com.redstore.data.OrderRequest
import com.redstore.data.Product
import com.redstore.data.RegisterRequest
import com.redstore.data.Session
import com.redstore.data.UpdateQtyRequest
import com.redstore.data.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class ShopUiState(
    val loading: Boolean = true,
    val busy: Boolean = false,
    val error: String? = null,
    val banner: String? = null,
    val promptLogin: Boolean = false,
    val lastOrderId: String? = null,
    val home: HomeResponse? = null,
    val products: List<Product> = emptyList(),
    val cart: CartResponse = CartResponse(),
    val wishlist: List<Product> = emptyList(),
    val orders: List<Order> = emptyList(),
    val user: UserDto? = null,
    val query: String = "",
    val category: String? = null,
    val serverUrl: String = BuildConfig.API_BASE_URL
) {
    val wishedIds: Set<Int> get() = wishlist.map { it.id }.toSet()

    val visibleProducts: List<Product>
        get() = products.filter { product ->
            (category == null || product.category.equals(category, ignoreCase = true)) &&
                (query.isBlank() ||
                    product.title.contains(query, ignoreCase = true) ||
                    product.brand.contains(query, ignoreCase = true) ||
                    product.category.contains(query, ignoreCase = true))
        }
}

class ShopViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences("redstore", Context.MODE_PRIVATE)
    private val _state = MutableStateFlow(ShopUiState())
    val state: StateFlow<ShopUiState> = _state.asStateFlow()

    init {
        Session.token = prefs.getString(KEY_TOKEN, null)
        val savedUrl = prefs.getString(KEY_URL, BuildConfig.API_BASE_URL) ?: BuildConfig.API_BASE_URL
        ApiFactory.updateBase(savedUrl)
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, serverUrl = ApiFactory.baseUrl) }
            try {
                val api = ApiFactory.api()
                val home = api.home()
                val products = api.products()
                var next = _state.value.copy(
                    loading = false,
                    error = null,
                    home = home,
                    products = products,
                    serverUrl = ApiFactory.baseUrl
                )
                if (Session.token != null) {
                    next = next.loadAccount()
                }
                _state.value = next
            } catch (error: Throwable) {
                if (error.isAuthFailure()) {
                    clearSession()
                }
                _state.update {
                    it.copy(loading = false, error = error.userMessage(), serverUrl = ApiFactory.baseUrl)
                }
            }
        }
    }

    fun setQuery(value: String) = _state.update { it.copy(query = value) }

    fun setCategory(value: String?) = _state.update { it.copy(category = value) }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(busy = true) }
            try {
                val auth = ApiFactory.api().login(LoginRequest(email.trim(), password))
                persist(auth.token)
                val next = _state.value.copy(
                    busy = false,
                    user = auth.user,
                    banner = "Welcome back, ${auth.user.name}"
                ).loadAccount()
                _state.value = next
            } catch (error: Throwable) {
                _state.update { it.copy(busy = false, banner = error.userMessage()) }
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(busy = true) }
            try {
                val auth = ApiFactory.api().register(RegisterRequest(name.trim(), email.trim(), password))
                persist(auth.token)
                val next = _state.value.copy(
                    busy = false,
                    user = auth.user,
                    banner = "Account created. Welcome, ${auth.user.name}"
                ).loadAccount()
                _state.value = next
            } catch (error: Throwable) {
                _state.update { it.copy(busy = false, banner = error.userMessage()) }
            }
        }
    }

    fun logout() {
        clearSession()
        _state.update {
            it.copy(
                user = null,
                cart = CartResponse(),
                wishlist = emptyList(),
                orders = emptyList(),
                banner = "Signed out"
            )
        }
    }

    fun addToCart(productId: Int, size: String, quantity: Int) {
        if (Session.token == null) {
            _state.update { it.copy(promptLogin = true, banner = "Sign in to add items to your bag") }
            return
        }
        viewModelScope.launch {
            try {
                val cart = ApiFactory.api().addToCart(AddCartRequest(productId, size, quantity))
                _state.update { it.copy(cart = cart, banner = "Added to your bag") }
            } catch (error: Throwable) {
                fail(error)
            }
        }
    }

    fun updateQuantity(lineId: String, quantity: Int) {
        if (quantity < 1) return
        viewModelScope.launch {
            try {
                val cart = ApiFactory.api().updateCart(lineId, UpdateQtyRequest(quantity))
                _state.update { it.copy(cart = cart) }
            } catch (error: Throwable) {
                fail(error)
            }
        }
    }

    fun removeFromCart(lineId: String) {
        viewModelScope.launch {
            try {
                val cart = ApiFactory.api().removeCart(lineId)
                _state.update { it.copy(cart = cart, banner = "Removed from bag") }
            } catch (error: Throwable) {
                fail(error)
            }
        }
    }

    fun toggleWishlist(product: Product) {
        if (Session.token == null) {
            _state.update { it.copy(promptLogin = true, banner = "Sign in to save items") }
            return
        }
        viewModelScope.launch {
            try {
                val wished = product.id in _state.value.wishedIds
                val wishlist = if (wished) {
                    ApiFactory.api().removeWish(product.id)
                } else {
                    ApiFactory.api().addWish(product.id)
                }
                val message = if (wished) "Removed from wishlist" else "Saved to wishlist"
                _state.update { it.copy(wishlist = wishlist, banner = message) }
            } catch (error: Throwable) {
                fail(error)
            }
        }
    }

    fun placeOrder(request: OrderRequest) {
        viewModelScope.launch {
            _state.update { it.copy(busy = true) }
            try {
                val api = ApiFactory.api()
                val order = api.placeOrder(request)
                _state.update {
                    it.copy(
                        busy = false,
                        cart = CartResponse(),
                        orders = api.orders(),
                        lastOrderId = order.id,
                        banner = "Order ${order.id} placed"
                    )
                }
            } catch (error: Throwable) {
                _state.update { it.copy(busy = false, banner = error.userMessage()) }
            }
        }
    }

    fun setServer(url: String) {
        val trimmed = url.trim()
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            _state.update { it.copy(banner = "Server address must start with http:// or https://") }
            return
        }
        prefs.edit().putString(KEY_URL, trimmed).apply()
        ApiFactory.updateBase(trimmed)
        refresh()
    }

    fun consumeBanner() = _state.update { it.copy(banner = null) }
    fun consumePrompt() = _state.update { it.copy(promptLogin = false) }
    fun consumeOrderNav() = _state.update { it.copy(lastOrderId = null) }

    private suspend fun ShopUiState.loadAccount(): ShopUiState {
        val api = ApiFactory.api()
        return copy(
            user = api.me(),
            cart = api.cart(),
            wishlist = api.wishlist(),
            orders = api.orders()
        )
    }

    private fun persist(token: String) {
        Session.token = token
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    private fun clearSession() {
        Session.token = null
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    private fun fail(error: Throwable) {
        if (error.isAuthFailure()) {
            clearSession()
            _state.update {
                it.copy(
                    user = null,
                    cart = CartResponse(),
                    wishlist = emptyList(),
                    promptLogin = true,
                    banner = "Sign in to continue"
                )
            }
        } else {
            _state.update { it.copy(banner = error.userMessage()) }
        }
    }

    private fun Throwable.userMessage(): String {
        if (this is IOException) {
            return "Can't reach RedStore. Start the backend, then retry."
        }
        if (this is HttpException) {
            val raw = response()?.errorBody()?.string().orEmpty()
            val parsed = runCatching { Gson().fromJson(raw, ApiError::class.java).error }.getOrNull()
            if (!parsed.isNullOrBlank()) return parsed
            return "Request failed (${code()})"
        }
        return message ?: "Something went wrong"
    }

    private fun Throwable.isAuthFailure(): Boolean = this is HttpException && code() == 401

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_URL = "baseUrl"
    }
}
