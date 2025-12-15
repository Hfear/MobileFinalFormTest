package com.example.mobileformtest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobileformtest.model.Car
import com.example.mobileformtest.model.PartCategory
import androidx.compose.material.icons.automirrored.filled.ArrowBack

/**
 * Car Detail Screen
 * Shows detailed information about a specific car and its parts
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailScreen(
    car: Car,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<PartCategory?>(null) }

    Scaffold(
        topBar = {
            Box {
                // Slanted colored header background (same as Home screen)
                SlantedHeaderBackground(
                    modifier = Modifier.fillMaxWidth()
                )

                TopAppBar(
                    title = {
                        Text(
                            text = "${car.year} ${car.make} ${car.model}",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        scrolledContainerColor = Color.Transparent
                    )
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Car Image Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = car.make,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = car.model,
                                    fontSize = 24.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${car.year}",
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // Car Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Vehicle Details",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider()

                        InfoRow(label = "Make", value = car.make)
                        InfoRow(label = "Model", value = car.model)
                        InfoRow(label = "Year", value = car.year.toString())
                        InfoRow(label = "Parts Available", value = "${car.parts.size}")
                        InfoRow(
                            label = "In Stock",
                            value = "${car.parts.count { it.inStock }}"
                        )
                    }
                }
            }

            // Category Filter Chips
            item {
                Column {
                    Text(
                        text = "Filter by Category",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("All") }
                        )
                        FilterChip(
                            selected = selectedCategory == PartCategory.ENGINE,
                            onClick = {
                                selectedCategory = if (selectedCategory == PartCategory.ENGINE) {
                                    null
                                } else {
                                    PartCategory.ENGINE
                                }
                            },
                            label = { Text("Engine") }
                        )
                        FilterChip(
                            selected = selectedCategory == PartCategory.BRAKES,
                            onClick = {
                                selectedCategory = if (selectedCategory == PartCategory.BRAKES) {
                                    null
                                } else {
                                    PartCategory.BRAKES
                                }
                            },
                            label = { Text("Brakes") }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedCategory == PartCategory.WHEELS,
                            onClick = {
                                selectedCategory = if (selectedCategory == PartCategory.WHEELS) {
                                    null
                                } else {
                                    PartCategory.WHEELS
                                }
                            },
                            label = { Text("Wheels") }
                        )
                        FilterChip(
                            selected = selectedCategory == PartCategory.TRANSMISSION,
                            onClick = {
                                selectedCategory = if (selectedCategory == PartCategory.TRANSMISSION) {
                                    null
                                } else {
                                    PartCategory.TRANSMISSION
                                }
                            },
                            label = { Text("Trans") }
                        )
                    }
                }
            }

            // Parts List Header
            item {
                Text(
                    text = "Available Parts",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Parts List
            val filteredParts = if (selectedCategory != null) {
                car.parts.filter { it.getCategoryEnum() == selectedCategory }
            } else {
                car.parts
            }

            items(filteredParts) { part ->
                PartCard(part = part)
            }

            // Empty state if no parts match filter
            if (filteredParts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No parts found in this category",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun PartCard(part: com.example.mobileformtest.model.CarPart) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (part.inStock) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = part.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Badge
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = part.category,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Stock Status
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (part.inStock) {
                                Icons.Default.CheckCircle
                            } else {
                                Icons.Default.Warning
                            },
                            contentDescription = if (part.inStock) "In Stock" else "Out of Stock",
                            tint = if (part.inStock) {
                                Color(0xFF4CAF50)
                            } else {
                                Color(0xFFFFA726)
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (part.inStock) "In Stock" else "Out of Stock",
                            fontSize = 12.sp,
                            color = if (part.inStock) {
                                Color(0xFF4CAF50)
                            } else {
                                Color(0xFFFFA726)
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Price
            Text(
                text = "$${String.format("%.2f", part.price)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
