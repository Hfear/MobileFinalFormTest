package com.example.mobileformtest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobileformtest.R
import com.example.mobileformtest.model.Car
import com.example.mobileformtest.ui.CarUiState
import com.example.mobileformtest.ui.CarViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path

/**
 * Main Home Screen
 * Uses when expression to handle different UI states (Loading, Success, Error)
 * Following the pattern from Mars Photos app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCarClick: (Car) -> Unit,
    viewModel: CarViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Box {
                // SLANTED STRIPES BEHIND
                SlantedHeaderBackground(
                    modifier = Modifier.fillMaxWidth()
                )

                TopAppBar(
                    title = {
                        Text(
                            text = "Car Parts Catalog",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshData() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh data"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        scrolledContainerColor = Color.Transparent
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchCars(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Find a car...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(28.dp)
            )

            // When expression checking carUiState
            // Following the pattern from Mars Photos app
            when (val state = viewModel.carUiState) {
                is CarUiState.Loading -> {
                    LoadingScreen(modifier = Modifier.fillMaxSize())
                }

                is CarUiState.Success -> {
                    ResultScreen(
                        cars = state.cars,
                        onCarClick = onCarClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                is CarUiState.Error -> {
                    ErrorScreen(
                        retryAction = { viewModel.getCars() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
@Composable
fun SlantedHeaderBackground(
    modifier: Modifier = Modifier,
    color1: Color = Color(0xFF1F3459),
    color2: Color = Color(0xFF4F8C9B),
    color3: Color = Color(0xFF89A7A3)
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        val stripeHeight = size.height * 0.70f
        val stripeWidth = stripeHeight * 0.85f
        val startY = (size.height - stripeHeight) / 2f
        val slant = stripeWidth * 0.35f

        fun stripePath(offsetX: Float, width: Float): Path {
            return Path().apply {
                moveTo(offsetX, startY + stripeHeight)
                lineTo(offsetX + slant, startY)
                lineTo(offsetX + slant + width, startY)
                lineTo(offsetX + width, startY + stripeHeight)
                close()
            }
        }

        val firstOffset = 0f
        val secondOffset = stripeWidth * 0.9f
        val thirdOffset = stripeWidth * 1.8f

        drawPath(stripePath(firstOffset, stripeWidth), color1)
        drawPath(stripePath(secondOffset, stripeWidth), color2)
        drawPath(stripePath(thirdOffset, stripeWidth * 6.5f), color3)
    }
}

/**
 * Loading Screen - shown while data is being fetched
 */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Loading cars...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Error Screen - shown when data loading fails
 * Includes retry button
 */
@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Failed to load",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )

        Text(
            text = "Check your data source and try again",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = retryAction,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Retry")
        }
    }
}

/**
 * Result Screen - displays the list of cars
 * Shown when data is successfully loaded
 */
@Composable
fun ResultScreen(
    cars: List<Car>,
    onCarClick: (Car) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Results Count
        Text(
            text = "${cars.size} car${if (cars.size != 1) "s" else ""} found",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        if (cars.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No cars found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            // Car List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cars) { car ->
                    CarListItem(
                        car = car,
                        onClick = { onCarClick(car) }
                    )
                }
            }
        }
    }
}

/**
 * Individual car list item
 */
@Composable
fun CarListItem(
    car: Car,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Car Image/Icon Placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = car.make.take(1),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Car Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${car.year} ${car.make}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = car.model,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    // Parts count badge
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${car.parts.size} parts",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    // In stock badge
                    val inStockCount = car.parts.count { it.inStock }
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "$inStockCount in stock",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
}