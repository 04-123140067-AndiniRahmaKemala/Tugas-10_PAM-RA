package org.tugas3.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.tugas3.project.data.*

data class AiUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage("Halo! Saya asisten AI Note App. Saya bisa membantu Anda **meringkas catatan** atau **menerjemahkan teks**. Tempelkan teks Anda!", isUser = false)
    ),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

class AiViewModel(private val geminiService: GeminiService) : ViewModel() {
    private val _uiState = MutableStateFlow(AiUiState())
    val uiState = _uiState.asStateFlow()

    private val systemPrompt = "Anda adalah pakar konten catatan. Bantu ringkas atau terjemahkan teks dengan akurat dan profesional dalam Bahasa Indonesia."

    fun sendMessage(text: String) {
        processAiRequest(text)
    }

    fun summarizeText(text: String) {
        processAiRequest("Ringkas teks ini menjadi poin-poin utama:\n\n$text")
    }

    fun translateText(text: String, targetLang: String) {
        processAiRequest("Terjemahkan teks ini ke $targetLang:\n\n$text")
    }

    private fun processAiRequest(text: String) {
        if (text.isBlank() || _uiState.value.isLoading) return

        val userMessage = ChatMessage(text, isUser = true)
        _uiState.update { it.copy(
            messages = it.messages + userMessage,
            isLoading = true,
            error = null
        ) }

        viewModelScope.launch {
            try {
                val validHistory = mutableListOf<Content>()
                
                // Ambil semua pesan chat, lewati pesan sambutan di index 0
                val allChatMessages = _uiState.value.messages.drop(1)
                
                allChatMessages.forEach { msg ->
                    val role = if (msg.isUser) "user" else "model"
                    
                    // Logika: Gabungkan pesan berturut-turut dengan role yang sama
                    if (validHistory.isNotEmpty() && validHistory.last().role == role) {
                        val last = validHistory.removeAt(validHistory.size - 1)
                        val combinedText = last.parts.first().text + "\n" + msg.text
                        validHistory.add(Content(role = role, parts = listOf(Part(combinedText))))
                    } else {
                        validHistory.add(Content(role = role, parts = listOf(Part(msg.text))))
                    }
                }

                // Pastikan percakapan dimulai oleh user (Syarat Gemini API)
                if (validHistory.isNotEmpty() && validHistory.first().role != "user") {
                    validHistory.removeAt(0)
                }

                if (validHistory.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false) }
                    return@launch
                }

                // Call generateContent with systemPrompt as second argument
                val result = geminiService.generateContent(validHistory, systemPrompt)
                
                result.onSuccess { aiResponse ->
                    _uiState.update { it.copy(
                        messages = it.messages + ChatMessage(aiResponse, isUser = false),
                        isLoading = false
                    ) }
                }.onFailure { e ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "Gagal: ${e.message ?: "AI tidak memberikan respon"}"
                    ) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Terjadi kesalahan: ${e.message}"
                ) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
