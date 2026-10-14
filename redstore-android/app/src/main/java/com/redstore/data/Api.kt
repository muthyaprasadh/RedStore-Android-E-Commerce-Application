package com.redstore.data

import com.redstore.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

object Session {
    @Volatile
    var token: String? = null
}

object ApiFactory {
    @Volatile
    var baseUrl: String = BuildConfig.API_BASE_URL

    @Volatile
    private var cached: RedStoreApi? = null

    fun updateBase(url: String) {
        val normalized = if (url.endsWith("/")) url else "$url/"
        if (normalized != baseUrl) {
            baseUrl = normalized
            cached = null
        }
    }

    fun api(): RedStoreApi {
        cached?.let { return it }
        return synchronized(this) {
            cached ?: build().also { cached = it }
        }
    }

    private fun build(): RedStoreApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                Session.token?.let { builder.header("Authorization", "Bearer $it") }
                chain.proceed(builder.build())
            }
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RedStoreApi::class.java)
    }
}

fun String.asImageUrl(): String {
    if (startsWith("http")) return this
    val base = ApiFactory.baseUrl
    return if (startsWith("/")) base.trimEnd('/') + this else base + this
}

interface RedStoreApi {
    @GET("api/home")
    suspend fun home(): HomeResponse

    @GET("api/products")
    suspend fun products(): List<Product>

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @GET("api/me")
    suspend fun me(): UserDto

    @GET("api/cart")
    suspend fun cart(): CartResponse

    @POST("api/cart")
    suspend fun addToCart(@Body body: AddCartRequest): CartResponse

    @PUT("api/cart/{id}")
    suspend fun updateCart(@Path("id") id: String, @Body body: UpdateQtyRequest): CartResponse

    @DELETE("api/cart/{id}")
    suspend fun removeCart(@Path("id") id: String): CartResponse

    @GET("api/wishlist")
    suspend fun wishlist(): List<Product>

    @POST("api/wishlist/{id}")
    suspend fun addWish(@Path("id") id: Int): List<Product>

    @DELETE("api/wishlist/{id}")
    suspend fun removeWish(@Path("id") id: Int): List<Product>

    @GET("api/orders")
    suspend fun orders(): List<Order>

    @POST("api/orders")
    suspend fun placeOrder(@Body body: OrderRequest): Order
}
