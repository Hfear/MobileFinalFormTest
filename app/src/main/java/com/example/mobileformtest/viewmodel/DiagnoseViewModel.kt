package com.example.mobileformtest.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileformtest.BuildConfig
import com.google.ai.client.generativeai.BuildConfig as GBuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Exchange(
    val prompt: String,
    val response: String
)

data class DiagnoseUiState(
    val prompt: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val exchanges: List<Exchange> = emptyList()
)

class DiagnoseViewModel : ViewModel() {

    private val hasApiKey = BuildConfig.GEMINI_API_KEY.isNotBlank()

    private val generativeModel: GenerativeModel? = if (hasApiKey) {
        // You can change this model name later if you want.
        GenerativeModel(
            modelName = "gemini-2.5-pro",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    } else {
        null
    }

    private val chat: Chat? = generativeModel?.startChat()

    @Suppress("unused")
    private val genAiLibVersionDebug: String = runCatching { GBuildConfig.VERSION_NAME }.getOrDefault("unknown")

    var uiState by mutableStateOf(DiagnoseUiState())
        private set

    val isApiKeyMissing: Boolean
        get() = !hasApiKey

    fun updatePrompt(value: String) {
        uiState = uiState.copy(prompt = value, errorMessage = null)
    }

    fun submitPrompt() {
        val trimmedPrompt = uiState.prompt.trim()
        if (trimmedPrompt.isEmpty()) {
            uiState = uiState.copy(errorMessage = "Please describe your issue before submitting.")
            return
        }

        val model = generativeModel
        if (model == null) {
            uiState = uiState.copy(
                errorMessage = "Gemini API key not configured. Add GEMINI_API_KEY to local.properties."
            )
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val systemPrompt =
                    "You are an automotive diagnostic assistant. Give 2-4 likely causes and actionable steps. If code/part unknown, ask for more details. Respond in Markdown with concise headings and bullet lists."

                val fullPrompt = """
                    $systemPrompt

                    User issue: $trimmedPrompt
                """.trimIndent()

                val reply = withContext(Dispatchers.IO) {
                    val response = if (chat != null) {
                        chat.sendMessage(fullPrompt)
                    } else {
                        model.generateContent(fullPrompt)
                    }

                    response.text?.trim()
                        .takeUnless { it.isNullOrBlank() }
                        ?: "No response from model. Please try again."
                }

                uiState = uiState.copy(
                    prompt = "",
                    isLoading = false,
                    exchanges = uiState.exchanges + Exchange(trimmedPrompt, reply)
                )
            } catch (e: Exception) {
                val friendlyMessage = e.localizedMessage?.let { "Request failed: $it" }
                    ?: "Request failed. Please try again."
                uiState = uiState.copy(isLoading = false, errorMessage = friendlyMessage)
            }
        }
    }

    fun dismissError() {
        uiState = uiState.copy(errorMessage = null)
    }
}
