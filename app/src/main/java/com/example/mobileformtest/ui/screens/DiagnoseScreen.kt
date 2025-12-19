package com.example.mobileformtest.ui.screens

import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobileformtest.viewmodel.DiagnoseViewModel
import com.example.mobileformtest.viewmodel.Exchange
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnoseScreen(viewModel: DiagnoseViewModel = viewModel()) {
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()
    val suggestions = listOf(
        "2007 Camry rear O2 sensor code",
        "Rough idle after cold start",
        "Brake pedal feels spongy after pad change"
    )

    Scaffold(
        topBar = {
            Box {
                SlantedHeaderBackground(modifier = Modifier.fillMaxWidth())
                TopAppBar(
                    title = {
                        Text(
                            text = "Diagnose",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Describe the issue and get quick troubleshooting steps.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            if (viewModel.isApiKeyMissing) {
                AssistiveMessage(
                    text = "Gemini API key not configured. Add GEMINI_API_KEY to local.properties and rebuild.",
                    isError = true
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestions.forEach { suggestion ->
                    AssistChip(
                        onClick = { viewModel.updatePrompt(suggestion) },
                        label = { Text(suggestion) }
                    )
                }
            }

            OutlinedTextField(
                value = uiState.prompt,
                onValueChange = { viewModel.updatePrompt(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp),
                placeholder = { Text("E.g. 2007 Camry rear O2 sensor code") },
                label = { Text("Describe the symptom") },
                singleLine = false,
                maxLines = 6
            )

            Button(
                onClick = { viewModel.submitPrompt() },
                enabled = !uiState.isLoading && uiState.prompt.isNotBlank() && !viewModel.isApiKeyMissing,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Get suggestions")
            }

            if (uiState.isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Text("Contacting Gemini...", style = MaterialTheme.typography.bodyMedium)
                }
            }

            uiState.errorMessage?.let { message ->
                AssistiveMessage(text = message, isError = true)
            }

            if (uiState.exchanges.isEmpty()) {
                EmptyStateCard()
            } else {
                uiState.exchanges.reversed().forEach { exchange ->
                    DiagnoseExchangeCard(exchange = exchange)
                }
            }
        }
    }
}

@Composable
private fun AssistiveMessage(text: String, isError: Boolean) {
    val colors = if (isError) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = colors
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Your suggestions will appear here.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Submit a vehicle symptom to see likely causes and next steps.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DiagnoseExchangeCard(exchange: Exchange) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "You",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = exchange.prompt,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Divider()
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Text(
                    text = "Gemini",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            DiagnoseResponseText(response = exchange.response)
        }
    }
}

@Composable
private fun DiagnoseResponseText(response: String) {
    MarkdownText(markdown = response)
}

@Composable
private fun MarkdownText(markdown: String, modifier: Modifier = Modifier) {
    val parser = remember { Parser.builder().build() }
    val renderer = remember { HtmlRenderer.builder().escapeHtml(false).build() }
    val density = LocalDensity.current
    val bodyStyle = MaterialTheme.typography.bodyMedium
    val textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f).toArgb()

    val html = remember(markdown) {
        val document = parser.parse(markdown)
        renderer.render(document)
    }

    val textSizePx = with(density) { bodyStyle.fontSize.toPx() }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            TextView(context).apply {
                movementMethod = LinkMovementMethod.getInstance()
                setLineSpacing(0f, 1.2f)
            }
        },
        update = { textView ->
            textView.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
            textView.setTextColor(textColor)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx)
        }
    )
}
