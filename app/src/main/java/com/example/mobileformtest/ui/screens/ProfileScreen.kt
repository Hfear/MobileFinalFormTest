package com.example.mobileformtest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// REMOVED ProfileActivity - using Composable only now

@Composable
fun ProfileScreen(
    userEmail: String?,
    onSignOut: () -> Unit,
    onSignInRequest: () -> Unit
) {
    val isSignedIn = userEmail != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Profile Title
        Text(
            text = "Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isSignedIn) "Signed in" else "Guest",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userEmail ?: "You are currently browsing as a guest.",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (isSignedIn) {
                    Button(
                        onClick = onSignOut,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Sign Out")
                    }
                } else {
                    Button(
                        onClick = onSignInRequest,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Go to Sign In")
                    }
                }
            }
        }

        if (isSignedIn) {
            // Your Car(s) Section
            Text(
                text = "Your Car(s)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Car Images Grid (2x2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CarImageCard("Stock\nImage")
                    CarImageCard("Stock\nFacade")
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CarImageCard("Stock\nImage")
                    CarImageCard("Stock\nFacade")
                }
            }

            // Saved Parts Section
            Text(
                text = "Saved Parts",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Saved Parts Content",
                        fontSize = 16.sp
                    )
                }
            }

            // History Section
            Text(
                text = "History",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "History Content",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CarImageCard(label: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 14.sp
            )
        }
    }
}