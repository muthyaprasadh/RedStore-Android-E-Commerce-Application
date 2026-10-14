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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.redstore.data.Product
import com.redstore.data.asImageUrl
import java.util.Locale

@Composable
fun ProductScreen(
    product: Product,
    wished: Boolean,
    related: List<Product>,
    wishedIds: Set<Int>,
    onBack: () -> Unit,
    onAdd: (size: String, quantity: Int) -> Unit,
    onWish: () -> Unit,
    onRelatedWish: (Product) -> Unit,
    onProduct: (Int) -> Unit
) {
    var image by remember(product.id) { mutableStateOf(product.images.firstOrNull().orEmpty()) }
    var size by remember(product.id) { mutableStateOf(product.sizes.firstOrNull().orEmpty()) }
    var quantity by remember(product.id) { mutableIntStateOf(1) }

    LazyColumn(contentPadding = PaddingValues(bottom = 28.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(product.category, color = Muted, modifier = Modifier.weight(1f))
                IconButton(onClick = onWish) {
                    Icon(
                        imageVector = if (wished) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = Red
                    )
                }
            }
            AsyncImage(
                model = image.asImageUrl(),
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(340.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardWhite)
            )
            if (product.images.size > 1) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(product.images) { shot ->
                        AsyncImage(
                            model = shot.asImageUrl(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (shot == image) Red else CardWhite)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { image = shot }
                        )
                    }
                }
            }
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text(product.brand.uppercase(Locale.US), color = Red, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(product.title, fontWeight = FontWeight.Black, fontSize = 28.sp, lineHeight = 32.sp)
                Text(product.price.money(), fontWeight = FontWeight.Black, fontSize = 22.sp, modifier = Modifier.padding(top = 6.dp))
                Text(
                    String.format(Locale.US, "%.1f rating", product.rating),
                    color = Muted,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text("Size", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    product.sizes.forEach { option ->
                        val selected = option == size
                        Text(
                            option,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) Ink else CardWhite)
                                .clickable { size = option }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            color = if (selected) androidx.compose.ui.graphics.Color.White else Ink,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    QtyButton("-") { if (quantity > 1) quantity -= 1 }
                    Text("$quantity", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Black)
                    QtyButton("+") { quantity += 1 }
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { onAdd(size, quantity) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Red),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Add to bag", modifier = Modifier.padding(vertical = 6.dp))
                }
                Text("Product details", fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 22.dp))
                Text(product.description, color = Muted, modifier = Modifier.padding(top = 6.dp))
            }
        }
        if (related.isNotEmpty()) {
            item { SectionTitle("You may also like") }
            items(related.chunked(2)) { row ->
                ProductRow(row, wishedIds, onProduct, onRelatedWish)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun QtyButton(label: String, onClick: () -> Unit) {
    Text(
        label,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CardWhite)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        fontWeight = FontWeight.Black,
        fontSize = 18.sp
    )
}
