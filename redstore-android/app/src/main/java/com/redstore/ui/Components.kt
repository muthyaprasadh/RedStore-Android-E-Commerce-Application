package com.redstore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.redstore.data.Product
import com.redstore.data.asImageUrl
import java.util.Locale

fun Double.money(): String = String.format(Locale.US, "$%.2f", this)

@Composable
fun ProductCard(
    product: Product,
    wished: Boolean,
    onClick: () -> Unit,
    onWish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box {
            AsyncImage(
                model = product.images.firstOrNull()?.asImageUrl(),
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Paper)
            )
            IconButton(
                onClick = onWish,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if (wished) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Wishlist",
                    tint = Red
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(product.brand.uppercase(Locale.US), color = Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(
            product.title,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 14.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Star, contentDescription = null, tint = Red, modifier = Modifier.size(14.dp))
            Text(" ${String.format(Locale.US, "%.1f", product.rating)}", fontSize = 12.sp, color = Muted)
        }
        Text(product.price.money(), fontWeight = FontWeight.Black, fontSize = 16.sp)
    }
}

@Composable
fun ProductRow(
    products: List<Product>,
    wishedIds: Set<Int>,
    onProduct: (Int) -> Unit,
    onWish: (Product) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        products.forEach { product ->
            ProductCard(
                product = product,
                wished = product.id in wishedIds,
                onClick = { onProduct(product.id) },
                onWish = { onWish(product) },
                modifier = Modifier.weight(1f)
            )
        }
        if (products.size == 1) Spacer(Modifier.weight(1f))
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 12.dp),
        fontWeight = FontWeight.Black,
        fontSize = 22.sp,
        color = Ink
    )
}

@Composable
fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Red)
    }
}

@Composable
fun ErrorState(message: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("RedStore is offline", fontWeight = FontWeight.Black, fontSize = 24.sp)
        Spacer(Modifier.height(8.dp))
        Text(message ?: "Start the backend and try again.", color = Muted)
        Spacer(Modifier.height(18.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Red)) {
            Text("Retry")
        }
    }
}

@Composable
fun EmptyState(title: String, body: String, action: String, onAction: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontWeight = FontWeight.Black, fontSize = 26.sp)
        Spacer(Modifier.height(8.dp))
        Text(body, color = Muted)
        Spacer(Modifier.height(18.dp))
        Button(onClick = onAction, colors = ButtonDefaults.buttonColors(containerColor = Red)) {
            Text(action)
        }
    }
}
