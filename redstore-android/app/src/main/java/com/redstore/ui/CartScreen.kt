package com.redstore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.redstore.data.OrderRequest
import com.redstore.data.asImageUrl

@Composable
fun CartScreen(
    state: ShopUiState,
    onShop: () -> Unit,
    onQty: (String, Int) -> Unit,
    onRemove: (String) -> Unit,
    onCheckout: () -> Unit
) {
    if (state.user == null) {
        EmptyState(
            title = "Sign in to see your bag",
            body = "Your cart is saved to your RedStore account.",
            action = "Go to account",
            onAction = onShop
        )
        return
    }
    if (state.cart.items.isEmpty()) {
        EmptyState(
            title = "Your bag is empty",
            body = "Featured tees, shoes, and the Smart Band are waiting.",
            action = "Start shopping",
            onAction = onShop
        )
        return
    }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Shopping bag", fontWeight = FontWeight.Black, fontSize = 28.sp)
            Text("${state.cart.count} items", color = Muted)
        }
        items(state.cart.items, key = { it.id }) { line ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardWhite)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = line.image.asImageUrl(),
                    contentDescription = line.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text(line.brand, color = Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(line.title, fontWeight = FontWeight.Bold, maxLines = 2)
                    Text("Size ${line.size}", color = Muted, fontSize = 12.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                        Text(
                            "−",
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Paper)
                                .clickable { onQty(line.id, line.quantity - 1) }
                                .padding(horizontal = 10.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Black
                        )
                        Text("${line.quantity}", modifier = Modifier.padding(horizontal = 10.dp))
                        Text(
                            "+",
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Paper)
                                .clickable { onQty(line.id, line.quantity + 1) }
                                .padding(horizontal = 10.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(line.lineTotal.money(), fontWeight = FontWeight.Black)
                    IconButton(onClick = { onRemove(line.id) }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Remove", tint = Muted)
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardWhite)
                    .padding(16.dp)
            ) {
                SummaryLine("Subtotal", state.cart.subtotal.money())
                SummaryLine("Shipping", if (state.cart.shipping == 0.0) "Free" else state.cart.shipping.money())
                Text("Free shipping on orders of $100 or more", color = Muted, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                SummaryLine("Total", state.cart.total.money(), bold = true)
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onCheckout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Red),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Checkout")
                }
            }
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = if (bold) FontWeight.Black else FontWeight.Medium)
        Text(value, fontWeight = if (bold) FontWeight.Black else FontWeight.SemiBold)
    }
}

@Composable
fun CheckoutScreen(
    state: ShopUiState,
    onBack: () -> Unit,
    onPlace: (OrderRequest) -> Unit
) {
    var name by remember(state.user?.name) { mutableStateOf(state.user?.name.orEmpty()) }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("Cash on delivery") }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("← Back", modifier = Modifier.clickable(onClick = onBack), color = Red, fontWeight = FontWeight.Bold)
            Text("Checkout", fontWeight = FontWeight.Black, fontSize = 28.sp)
            Text("Total ${state.cart.total.money()}", color = Muted)
        }
        item {
            OutlinedTextField(name, { name = it }, label = { Text("Full name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }
        item {
            OutlinedTextField(phone, { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }
        item {
            OutlinedTextField(address, { address = it }, label = { Text("Delivery address") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        }
        item {
            Text("Payment", fontWeight = FontWeight.Bold)
            listOf("Cash on delivery", "Card on delivery").forEach { option ->
                val selected = method == option
                Text(
                    option,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) Ink else CardWhite)
                        .clickable { method = option }
                        .padding(14.dp),
                    color = if (selected) androidx.compose.ui.graphics.Color.White else Ink
                )
            }
        }
        item {
            Button(
                onClick = {
                    onPlace(OrderRequest(name.trim(), phone.trim(), address.trim(), method))
                },
                enabled = !state.busy,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Red),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(if (state.busy) "Placing order..." else "Place order")
            }
            Spacer(Modifier.width(1.dp))
        }
    }
}

@Composable
fun WishlistScreen(
    state: ShopUiState,
    onShop: () -> Unit,
    onAccount: () -> Unit,
    onProduct: (Int) -> Unit,
    onWish: (com.redstore.data.Product) -> Unit
) {
    if (state.user == null) {
        EmptyState("Sign in to view your wishlist", "Saved pieces stay with your account.", "Go to account", onAccount)
        return
    }
    if (state.wishlist.isEmpty()) {
        EmptyState("Nothing saved yet", "Tap the heart on a product to keep it here.", "Browse products", onShop)
        return
    }
    LazyColumn(contentPadding = PaddingValues(bottom = 20.dp)) {
        item {
            Text(
                "Wishlist",
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Black,
                fontSize = 28.sp
            )
        }
        items(state.wishlist.chunked(2)) { row ->
            ProductRow(row, state.wishedIds, onProduct, onWish)
            Spacer(Modifier.height(12.dp))
        }
    }
}
