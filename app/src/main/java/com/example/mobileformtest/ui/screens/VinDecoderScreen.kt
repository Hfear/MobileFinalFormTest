package com.example.mobileformtest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobileformtest.model.DecodedVehicle
import com.example.mobileformtest.ui.VinUiState
import com.example.mobileformtest.ui.VinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VinDecoderScreen(
    onVehicleSaved: (DecodedVehicle) -> Unit,
    viewModel: VinViewModel,
    currentUserId: String? = null
) {
    var vinInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "VIN Decoder",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Enter Your Vehicle's VIN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "A VIN (Vehicle Identification Number) is a 17-character code. You can find it on your vehicle's dashboard, driver's side door, or registration.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            OutlinedTextField(
                value = vinInput,
                onValueChange = {
                    val cleaned = it.uppercase().filter { char -> char.isLetterOrDigit() }.take(17)
                    vinInput = cleaned
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("VIN Number") },
                placeholder = { Text("e.g., 1HGBH41JXMN109186") },
                singleLine = true,
                supportingText = {
                    Text("${vinInput.length}/17 characters")
                },
                isError = vinInput.isNotEmpty() && vinInput.length < 17
            )

            Button(
                onClick = {
                    viewModel.decodeVin(vinInput)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = vinInput.length >= 11
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Decode VIN", style = MaterialTheme.typography.titleMedium)
            }

            when (val state = viewModel.vinUiState) {
                is VinUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Decoding VIN...")
                        }
                    }
                }

                is VinUiState.Success -> {
                    DecodedVehicleCard(
                        vehicle = state.vehicle,
                        onSaveClick = {
                            viewModel.saveVehicleToProfile(state.vehicle, currentUserId)
                            onVehicleSaved(state.vehicle)
                        }
                    )
                }

                is VinUiState.Error -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Error",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                is VinUiState.Idle -> { }
            }
        }
    }
}

@Composable
fun DecodedVehicleCard(
    vehicle: DecodedVehicle,
    onSaveClick: () -> Unit
) {
    var saved by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Vehicle Decoded",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (saved) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Saved",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider()

            VehicleInfoRow("VIN", vehicle.vin)
            VehicleInfoRow("Make", vehicle.make)
            VehicleInfoRow("Model", vehicle.model)
            VehicleInfoRow("Year", vehicle.year)
            VehicleInfoRow("Type", vehicle.vehicleType)
            VehicleInfoRow("Manufacturer", vehicle.manufacturer)
            VehicleInfoRow("Plant Country", vehicle.plantCountry)
            VehicleInfoRow("Engine Cylinders", vehicle.engineInfo)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onSaveClick()
                    saved = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !saved
            ) {
                Text(if (saved) "Saved to My Vehicles" else "Save to My Vehicles")
            }
        }
    }
}

@Composable
fun VehicleInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1.5f)
        )
    }
}