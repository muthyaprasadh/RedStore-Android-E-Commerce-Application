package com.redstore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AccountScreen(
    state: ShopUiState,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String) -> Unit,
    onLogout: () -> Unit,
    onServer: (String) -> Unit,
    onRetry: () -> Unit
) {
    if (state.user == null) {
        AuthForm(state, onLogin, onRegister, onServer)
        return
    }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Account", fontWeight = FontWeight.Black, fontSize = 28.sp)
            Text(state.user.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(state.user.email, color = Muted)
        }
        item {
            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Sign out")
            }
        }
        item {
            Text("Orders", fontWeight = FontWeight.Black, fontSize = 22.sp)
            if (state.orders.isEmpty()) {
                Text("No orders yet.", color = Muted, modifier = Modifier.padding(top = 6.dp))
            }
        }
        items(state.orders) { order ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardWhite)
                    .padding(14.dp)
            ) {
                Text(order.id, fontWeight = FontWeight.Black, color = Red)
                Text(order.createdAt.take(16).replace("T", " "), color = Muted, fontSize = 12.sp)
                order.items.forEach { line ->
                    Text("${line.quantity} × ${line.title} (${line.size})", modifier = Modifier.padding(top = 4.dp))
                }
                Text(order.paymentMethod, color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
                Text(order.address, color = Muted, fontSize = 12.sp)
                Text(order.total.money(), fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 6.dp))
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Ink)) {
                Text("Refresh")
            }
        }
    }
}

@Composable
private fun AuthForm(
    state: ShopUiState,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String) -> Unit,
    onServer: (String) -> Unit
) {
    var mode by remember { mutableStateOf("login") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("demo@redstore.com") }
    var password by remember { mutableStateOf("password123") }
    var server by remember(state.serverUrl) { mutableStateOf(state.serverUrl) }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(if (mode == "login") "Welcome back" else "Create account", fontWeight = FontWeight.Black, fontSize = 28.sp)
            Text("Demo login is filled in. Password is password123.", color = Muted)
        }
        if (mode == "register") {
            item {
                OutlinedTextField(name, { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        }
        item {
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }
        item {
            OutlinedTextField(
                password,
                { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
        }
        item {
            Button(
                onClick = {
                    if (mode == "login") onLogin(email, password) else onRegister(name, email, password)
                },
                enabled = !state.busy,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Red),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(if (state.busy) "Please wait..." else if (mode == "login") "Sign in" else "Create account")
            }
            OutlinedButton(
                onClick = { mode = if (mode == "login") "register" else "login" },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(if (mode == "login") "Need an account? Register" else "Already shopping? Sign in")
            }
        }
        item {
            Spacer(Modifier.height(12.dp))
            Text("Server", fontWeight = FontWeight.Bold)
            Text(
                "Emulator default is http://10.0.2.2:8090/. On a phone, use your computer's LAN address, with the backend running.",
                color = Muted,
                fontSize = 12.sp
            )
            OutlinedTextField(
                server,
                { server = it },
                label = { Text("API base URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedButton(onClick = { onServer(server) }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Save server")
            }
        }
    }
}
