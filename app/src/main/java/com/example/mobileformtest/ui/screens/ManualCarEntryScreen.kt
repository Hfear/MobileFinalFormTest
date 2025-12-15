package com.example.mobileformtest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualCarEntryScreen(
    onBackClick: () -> Unit,
    onSaveClick: (Map<String, String>) -> Unit,
    currentUserId: String?
) {
    var vehicleType by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("") }
    var engineInfo by remember { mutableStateOf("") }
    var plantCountry by remember { mutableStateOf("") }
    var additionalNotes by remember { mutableStateOf("") }

    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Missing Info") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
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
            // Info banner
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        "Your input will be reviewed by our team before being added to the catalog.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Text(
                "Help us complete this vehicle's info",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Vehicle Type
            OutlinedTextField(
                value = vehicleType,
                onValueChange = { vehicleType = it },
                label = {
                    Text(
                        "Vehicle Type",
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                },
                placeholder = { Text("Sedan, SUV, Truck, etc.") },
                modifier = Modifier.fillMaxWidth()
            )

            // Manufacturer
            OutlinedTextField(
                value = manufacturer,
                onValueChange = { manufacturer = it },
                label = {
                    Text(
                        "Manufacturer",
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                },
                placeholder = { Text("Company name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Engine Info
            OutlinedTextField(
                value = engineInfo,
                onValueChange = { engineInfo = it },
                label = {
                    Text(
                        "Engine Cylinders",
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                },
                placeholder = { Text("4, 6, 8, etc.") },
                modifier = Modifier.fillMaxWidth()
            )

            // Plant Country
            OutlinedTextField(
                value = plantCountry,
                onValueChange = { plantCountry = it },
                label = {
                    Text(
                        "Manufactured In",
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                },
                placeholder = { Text("Country") },
                modifier = Modifier.fillMaxWidth()
            )

            // Additional Notes
            OutlinedTextField(
                value = additionalNotes,
                onValueChange = { additionalNotes = it },
                label = { Text("Additional Notes (optional)") },
                placeholder = { Text("Any other relevant info...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit button
            Button(
                onClick = {
                    isSaving = true
                    val updates = buildMap {
                        if (vehicleType.isNotBlank()) put("vehicleType", vehicleType)
                        if (manufacturer.isNotBlank()) put("manufacturer", manufacturer)
                        if (engineInfo.isNotBlank()) put("engineInfo", engineInfo)
                        if (plantCountry.isNotBlank()) put("plantCountry", plantCountry)
                        if (additionalNotes.isNotBlank()) put("notes", additionalNotes)
                    }
                    onSaveClick(updates)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isSaving && (vehicleType.isNotBlank() || manufacturer.isNotBlank() ||
                        engineInfo.isNotBlank() || plantCountry.isNotBlank())
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Submit Info", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}