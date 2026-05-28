package org.tugas3.project.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>
)

@Serializable
data class GroqMessage(
    val role: String,
    val content: String
)

@Serializable
data class GroqResponse(
    val choices: List<GroqChoice>? = null,
    val error: GroqError? = null
)

@Serializable
data class GroqChoice(
    val message: GroqMessage? = null,
    @SerialName("finish_reason")
    val finishReason: String? = null
)

@Serializable
data class GroqError(
    val message: String? = null,
    val type: String? = null
)

// Menjaga nama class tetap GeminiService agar tidak perlu merubah Koin/ViewModel
class GeminiService(private val apiKey: String) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
                encodeDefaults = false
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            connectTimeoutMillis = 30000
        }
    }

    // Adaptasi dari Gemini format ke Groq (OpenAI) format
    suspend fun generateContent(history: List<Content>, systemPrompt: String): Result<String> {
        if (apiKey.isBlank()) return Result.failure(Exception("API Key is missing"))
        
        return try {
            val url = "https://api.groq.com/openai/v1/chat/completions"
            
            // Konversi history Gemini ke format Message Groq
            val messages = mutableListOf<GroqMessage>()
            messages.add(GroqMessage(role = "system", content = systemPrompt))
            
            history.forEach { content ->
                val role = if (content.role == "model") "assistant" else "user"
                val text = content.parts.firstOrNull()?.text ?: ""
                messages.add(GroqMessage(role = role, content = text))
            }

            val response = client.post(url) {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(
                    GroqRequest(
                        // MENGGUNAKAN MODEL TERBARU (Llama 3.3 70B)
                        model = "llama-3.3-70b-versatile",
                        messages = messages
                    )
                )
            }

            if (response.status.isSuccess()) {
                val responseBody = response.body<GroqResponse>()
                val text = responseBody.choices?.firstOrNull()?.message?.content
                if (text != null) {
                    Result.success(text)
                } else {
                    Result.failure(Exception("AI did not return any text."))
                }
            } else {
                val errorBody = response.body<GroqResponse>()
                val errorMessage = errorBody.error?.message ?: response.body<String>()
                Result.failure(Exception("Groq Error ${response.status.value}: $errorMessage"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Tetap pertahankan class data lama agar tidak error di file lain yang mengimpornya
@Serializable
data class Content(
    val role: String? = null,
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)
