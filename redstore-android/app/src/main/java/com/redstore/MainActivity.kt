package com.redstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.redstore.ui.RedStoreApp
import com.redstore.ui.RedStoreTheme
import com.redstore.ui.ShopViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ShopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RedStoreTheme {
                RedStoreApp(viewModel)
            }
        }
    }
}
