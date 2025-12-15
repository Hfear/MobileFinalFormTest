package com.example.mobileformtest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobileformtest.data.SavedCarsRepository
import com.example.mobileformtest.model.Car
import com.example.mobileformtest.model.CarPart
import com.example.mobileformtest.model.PartCategory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailScreen(
    car: Car,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    currentUserId: String? = null,
    onAddMissingInfo: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf<PartCategory?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val savedCarsRepository = remember { SavedCarsRepository() }
    var isSaving by remember { mutableStateOf(false) }
    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }
    var saveErrorMessage by remember { mutableStateOf<String?>(null) }

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
            // Car image
            item {
                CarImageCard(car)
            }

            // Car info
            item {
                CarInfoCard(car)
            }

            // Save button
            item {
                SaveCarSection(
                    currentUserId = currentUserId,
                    car = car,
                    isSaving = isSaving,
                    saveSuccessMessage = saveSuccessMessage,
                    saveErrorMessage = saveErrorMessage,
                    onSaveClick = { userId ->
                        isSaving = true
                        saveSuccessMessage = null
                        saveErrorMessage = null
                        coroutineScope.launch {
                            try {
                                savedCarsRepository.saveCar(userId, car)
                                saveSuccessMessage = "Car saved to your garage"
                            } catch (e: Exception) {
                                saveErrorMessage = e.localizedMessage ?: "Unable to save car"
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    onError = { message ->
                        saveErrorMessage = message
                        saveSuccessMessage = null
                    }
                )
            }

            // Category filters
            item {
                CategoryFilterSection(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            // Parts header
            item {
                Text(
                    text = "Available Parts",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Parts list or empty state
            val filteredParts = if (selectedCategory != null) {
                car.parts.filter { it.getCategoryEnum() == selectedCategory }
            } else {
                car.parts
            }

            if (car.parts.isEmpty()) {
                item {
                    NoPartsAvailableCard(onAddInfoClick = onAddMissingInfo)
                }
            } else {
                items(filteredParts) { part ->
                    PartCard(part = part)
                }

                if (filteredParts.isEmpty()) {
                    item {
                        NoPartsInCategoryCard()
                    }
                }
            }
        }
    }
}

@Composable
private fun CarImageCard(car: Car) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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

@Composable
private fun CarInfoCard(car: Car) {
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
            InfoRow(label = "In Stock", value = "${car.parts.count { it.inStock }}")
        }
    }
}

@Composable
private fun SaveCarSection(
    currentUserId: String?,
    car: Car,
    isSaving: Boolean,
    saveSuccessMessage: String?,
    saveErrorMessage: String?,
    onSaveClick: (String) -> Unit,
    onError: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = {
                if (currentUserId == null) {
                    onError("Sign in to save cars")
                } else {
                    onSaveClick(currentUserId)
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text("Save Car")
            }
        }

        when {
            saveSuccessMessage != null -> Text(
                text = saveSuccessMessage,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
            saveErrorMessage != null -> Text(
                text = saveErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            currentUserId == null -> Text(
                text = "Sign in to save cars and parts",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun CategoryFilterSection(
    selectedCategory: PartCategory?,
    onCategorySelected: (PartCategory?) -> Unit
) {
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
                onClick = { onCategorySelected(null) },
                label = { Text("All") }
            )
            FilterChip(
                selected = selectedCategory == PartCategory.ENGINE,
                onClick = {
                    onCategorySelected(
                        if (selectedCategory == PartCategory.ENGINE) null
                        else PartCategory.ENGINE
                    )
                },
                label = { Text("Engine") }
            )
            FilterChip(
                selected = selectedCategory == PartCategory.BRAKES,
                onClick = {
                    onCategorySelected(
                        if (selectedCategory == PartCategory.BRAKES) null
                        else PartCategory.BRAKES
                    )
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
                    onCategorySelected(
                        if (selectedCategory == PartCategory.WHEELS) null
                        else PartCategory.WHEELS
                    )
                },
                label = { Text("Wheels") }
            )
            FilterChip(
                selected = selectedCategory == PartCategory.TRANSMISSION,
                onClick = {
                    onCategorySelected(
                        if (selectedCategory == PartCategory.TRANSMISSION) null
                        else PartCategory.TRANSMISSION
                    )
                },
                label = { Text("Trans") }
            )
        }
    }
}

@Composable
private fun NoPartsAvailableCard(onAddInfoClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "No parts available for this vehicle yet.",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "If you have missing details, add them to improve matches.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )

            Button(
                onClick = onAddInfoClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add Missing Info")
            }
        }
    }
}

@Composable
private fun NoPartsInCategoryCard() {
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
fun PartCard(part: CarPart) {
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

            Text(
                text = "$${String.format("%.2f", part.price)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
