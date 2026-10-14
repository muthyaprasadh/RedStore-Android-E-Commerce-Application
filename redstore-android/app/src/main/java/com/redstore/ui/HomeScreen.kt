package com.redstore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.redstore.data.asImageUrl

@Composable
fun HomeScreen(
    state: ShopUiState,
    onRetry: () -> Unit,
    onExplore: () -> Unit,
    onCategory: (String) -> Unit,
    onProduct: (Int) -> Unit,
    onWish: (com.redstore.data.Product) -> Unit
) {
    val home = state.home
    if (state.loading && home == null) {
        LoadingState()
        return
    }
    if (home == null) {
        ErrorState(state.error, onRetry)
        return
    }

    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(androidx.compose.ui.graphics.Color(0xFFFFF1EE))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1.1f)) {
                    Text("REDSTORE", color = Red, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Give Your Workout A New Style!",
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        lineHeight = 32.sp,
                        color = Ink
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Success isn’t always about greatness. It’s about consistency.",
                        color = Muted,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onExplore,
                        colors = ButtonDefaults.buttonColors(containerColor = Red),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Explore Now")
                    }
                }
                AsyncImage(
                    model = home.heroImage.asImageUrl(),
                    contentDescription = "New collection",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .weight(0.9f)
                        .height(220.dp)
                )
            }
        }

        item { SectionTitle("Categories") }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(home.categories) { category ->
                    Column(
                        modifier = Modifier
                            .width(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onCategory(category.name) }
                    ) {
                        AsyncImage(
                            model = category.image.asImageUrl(),
                            contentDescription = category.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        Text(
                            category.name,
                            modifier = Modifier.padding(top = 6.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item { SectionTitle("Featured Products") }
        items(home.featured.chunked(2)) { row ->
            ProductRow(row, state.wishedIds, onProduct, onWish)
            Spacer(Modifier.height(12.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Red)
                    .clickable { onProduct(home.offerProductId) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = home.offerImage.asImageUrl(),
                    contentDescription = home.offerTitle,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Column(Modifier.weight(1f).padding(start = 14.dp)) {
                    Text("Exclusive", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                    Text(home.offerTitle, color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
                    Text(home.offerText, color = androidx.compose.ui.graphics.Color.White, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }

        item { SectionTitle("Latest Products") }
        items(home.latest.chunked(2)) { row ->
            ProductRow(row, state.wishedIds, onProduct, onWish)
            Spacer(Modifier.height(12.dp))
        }

        item { SectionTitle("What shoppers say") }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(home.testimonials) { story ->
                    Column(
                        modifier = Modifier
                            .width(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardWhite)
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = story.image.asImageUrl(),
                                contentDescription = story.name,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(21.dp))
                            )
                            Column(Modifier.padding(start = 10.dp)) {
                                Text(story.name, fontWeight = FontWeight.Bold)
                                Text("${story.rating.toInt()} / 5", color = Red, fontSize = 12.sp)
                            }
                        }
                        Text(story.quote, modifier = Modifier.padding(top = 10.dp), color = Muted, fontSize = 13.sp)
                    }
                }
            }
        }

        item { SectionTitle("Brands") }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(home.brands) { logo ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardWhite)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        AsyncImage(
                            model = logo.asImageUrl(),
                            contentDescription = "Brand",
                            modifier = Modifier
                                .height(36.dp)
                                .width(88.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}
