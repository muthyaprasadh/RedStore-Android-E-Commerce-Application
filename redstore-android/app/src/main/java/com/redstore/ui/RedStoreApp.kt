package com.redstore.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab("home", "Home", Icons.Filled.Home),
    Tab("shop", "Shop", Icons.Outlined.ShoppingBag),
    Tab("cart", "Bag", Icons.Filled.ShoppingCart),
    Tab("wishlist", "Saved", Icons.Filled.Favorite),
    Tab("account", "Account", Icons.Filled.Person)
)

@Composable
fun RedStoreApp(viewModel: ShopViewModel) {
    val state by viewModel.state.collectAsState()
    val nav = rememberNavController()
    val snackbar = remember { SnackbarHostState() }
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route.orEmpty()
    val showBar = !route.startsWith("product") && route != "checkout"

    LaunchedEffect(state.banner) {
        val message = state.banner ?: return@LaunchedEffect
        snackbar.showSnackbar(message)
        viewModel.consumeBanner()
    }
    LaunchedEffect(state.promptLogin) {
        if (state.promptLogin) {
            nav.navigate("account") { launchSingleTop = true }
            viewModel.consumePrompt()
        }
    }
    LaunchedEffect(state.lastOrderId) {
        if (state.lastOrderId != null) {
            nav.navigate("account") {
                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
            viewModel.consumeOrderNav()
        }
    }

    Scaffold(
        containerColor = Paper,
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            if (showBar) {
                RedBottomBar(route, state.cart.count) { dest ->
                    nav.navigate(dest) {
                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                HomeScreen(
                    state = state,
                    onRetry = viewModel::refresh,
                    onExplore = { nav.openShop(null, viewModel) },
                    onCategory = { nav.openShop(it, viewModel) },
                    onProduct = { nav.openProduct(it) },
                    onWish = viewModel::toggleWishlist
                )
            }
            composable("shop") {
                ShopScreen(
                    state = state,
                    onQuery = viewModel::setQuery,
                    onCategory = viewModel::setCategory,
                    onProduct = { nav.openProduct(it) },
                    onWish = viewModel::toggleWishlist,
                    onRetry = viewModel::refresh
                )
            }
            composable("product/{id}") { entry ->
                val id = entry.arguments?.getString("id")?.toIntOrNull()
                val product = state.products.find { it.id == id }
                if (product == null) {
                    ErrorState("That product is not loaded yet.", viewModel::refresh)
                } else {
                    ProductScreen(
                        product = product,
                        wished = product.id in state.wishedIds,
                        related = state.products.filter { it.category == product.category && it.id != product.id }.take(4),
                        wishedIds = state.wishedIds,
                        onBack = { nav.popBackStack() },
                        onAdd = { size, qty -> viewModel.addToCart(product.id, size, qty) },
                        onWish = { viewModel.toggleWishlist(product) },
                        onRelatedWish = viewModel::toggleWishlist,
                        onProduct = { nav.openProduct(it) }
                    )
                }
            }
            composable("cart") {
                CartScreen(
                    state = state,
                    onShop = {
                        if (state.user == null) nav.navigate("account") { launchSingleTop = true }
                        else nav.openShop(null, viewModel)
                    },
                    onQty = viewModel::updateQuantity,
                    onRemove = viewModel::removeFromCart,
                    onCheckout = { nav.navigate("checkout") }
                )
            }
            composable("checkout") {
                CheckoutScreen(
                    state = state,
                    onBack = { nav.popBackStack() },
                    onPlace = viewModel::placeOrder
                )
            }
            composable("wishlist") {
                WishlistScreen(
                    state = state,
                    onShop = { nav.openShop(null, viewModel) },
                    onAccount = { nav.navigate("account") { launchSingleTop = true } },
                    onProduct = { nav.openProduct(it) },
                    onWish = viewModel::toggleWishlist
                )
            }
            composable("account") {
                AccountScreen(
                    state = state,
                    onLogin = viewModel::login,
                    onRegister = viewModel::register,
                    onLogout = viewModel::logout,
                    onServer = viewModel::setServer,
                    onRetry = viewModel::refresh
                )
            }
        }
    }
}

private fun NavHostController.openProduct(id: Int) {
    navigate("product/$id")
}

private fun NavHostController.openShop(category: String?, viewModel: ShopViewModel) {
    viewModel.setCategory(category)
    navigate("shop") {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun RedBottomBar(route: String, count: Int, onSelect: (String) -> Unit) {
    NavigationBar(containerColor = CardWhite) {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = route == tab.route,
                onClick = { onSelect(tab.route) },
                icon = {
                    if (tab.route == "cart" && count > 0) {
                        BadgedBox(badge = { Badge { Text(count.toString()) } }) {
                            Icon(tab.icon, contentDescription = tab.label)
                        }
                    } else {
                        Icon(tab.icon, contentDescription = tab.label)
                    }
                },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Red,
                    selectedTextColor = Red,
                    indicatorColor = Paper
                )
            )
        }
    }
}
