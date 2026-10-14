package com.redstore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShopScreen(
    state: ShopUiState,
    onQuery: (String) -> Unit,
    onCategory: (String?) -> Unit,
    onProduct: (Int) -> Unit,
    onWish: (com.redstore.data.Product) -> Unit,
    onRetry: () -> Unit
) {
    if (state.loading && state.products.isEmpty()) {
        LoadingState()
        return
    }
    if (state.home == null && state.products.isEmpty()) {
        ErrorState(state.error, onRetry)
        return
    }

    val categories = listOf("All") + (state.home?.categories?.map { it.name } ?: emptyList())
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(Modifier.padding(16.dp)) {
                Text("Shop", fontWeight = FontWeight.Black, fontSize = 28.sp)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = state.query,
                    onValueChange = onQuery,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Search shoes, tees, watches") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { })
                )
            }
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { name ->
                    val selected = if (name == "All") state.category == null else state.category == name
                    Text(
                        text = name,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) Red else CardWhite)
                            .clickable { onCategory(if (name == "All") null else name) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        color = if (selected) androidx.compose.ui.graphics.Color.White else Ink,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        item { Spacer(Modifier.height(14.dp)) }
        if (state.visibleProducts.isEmpty()) {
            item {
                Text(
                    "No products match that search.",
                    modifier = Modifier.padding(16.dp),
                    color = Muted
                )
            }
        } else {
            items(state.visibleProducts.chunked(2)) { row ->
                ProductRow(row, state.wishedIds, onProduct, onWish)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
