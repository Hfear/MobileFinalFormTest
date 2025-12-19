package com.example.mobileformtest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobileformtest.model.Car
import com.example.mobileformtest.model.CarPart
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailScreen(
    car: Car,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    onSaveCarClick: suspend (Car) -> Result<Unit> = { Result.success(Unit) },
    onAddMissingInfo: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var saving by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${car.year} ${car.make}",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = car.model,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAddMissingInfo) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Missing Info"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Save This Car",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Button(
                        onClick = {
                            scope.launch {
                                saving = true
                                val result = onSaveCarClick(car)
                                saving = false
                                snackbarHostState.showSnackbar(
                                    message = result.fold(
                                        onSuccess = { "Saved!" },
                                        onFailure = { "Save failed" }
                                    )
                                )
                            }
                        },
                        enabled = !saving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (saving) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Saving...")
                        } else {
                            Text("Save Car")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Available Parts (${car.parts.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (car.parts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No parts available for this car",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(car.parts) { part ->
                        PartCard(part)
                    }
                }
            }
        }
    }
}

@Composable
fun PartCard(part: CarPart) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = part.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$${part.price}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = if (part.inStock) "In Stock" else "Out of Stock",
                    color = if (part.inStock) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
