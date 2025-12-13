package com.example.mobileformtest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobileformtest.auth.FirebaseAuthManager

@Composable
fun ForgotPasswordScreen(
    authManager: FirebaseAuthManager,
    onBack: () -> Unit
) {
    val successColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var infoMessage by remember { mutableStateOf<String?>(null) }
    var messageColor by remember { mutableStateOf(successColor) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Enter the email associated with your account and we'll send you a reset link.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it.trimStart() },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Email") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (infoMessage != null) {
                    Text(
                        text = infoMessage!!,
                        color = messageColor,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Button(
                    onClick = {
                        if (email.isBlank()) {
                            messageColor = errorColor
                            infoMessage = "Email required"
                            return@Button
                        }
                        isLoading = true
                        infoMessage = null
                        authManager
                            .sendPasswordReset(email.trim())
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    messageColor = successColor
                                    infoMessage = "Password reset email sent"
                                } else {
                                    messageColor = errorColor
                                    infoMessage = task.exception?.localizedMessage
                                        ?: "Unable to send reset email"
                                }
                            }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(text = "Send Reset Email")
                    }
                }
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Back")
                }
            }
        }
    }
}
