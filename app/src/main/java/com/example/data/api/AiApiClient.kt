package com.example.data.api

import com.example.data.model.ModelInfo
import com.example.data.model.ProviderType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object AiApiClient {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Model Discovery Engine
    fun discoverModelsForProvider(providerType: ProviderType, apiKey: String, baseUrl: String = ""): List<ModelInfo> {
        return when (providerType) {
            ProviderType.NVIDIA_NIM -> listOf(
                ModelInfo("nvidia/nemotron-4-340b-instruct", "NVIDIA Nemotron 4 340B", ProviderType.NVIDIA_NIM, "NVIDIA NIM", "State-of-the-art synthetic data & instruction model", "128k tokens", supportsReasoning = true, pricingInfo = "NVIDIA Cloud Credits"),
                ModelInfo("meta/llama-3.3-70b-instruct", "Llama 3.3 70B (NVIDIA)", ProviderType.NVIDIA_NIM, "NVIDIA NIM", "Highly scalable open weights foundation model", "128k tokens", pricingInfo = "NVIDIA Cloud Credits"),
                ModelInfo("deepseek-ai/deepseek-r1", "DeepSeek R1 (NVIDIA)", ProviderType.NVIDIA_NIM, "NVIDIA NIM", "Advanced reasoning & chain-of-thought model", "64k tokens", supportsReasoning = true, pricingInfo = "NVIDIA Cloud Credits"),
                ModelInfo("mistralai/mistral-large-2-instruct", "Mistral Large 2", ProviderType.NVIDIA_NIM, "NVIDIA NIM", "Multilingual, reasoning and coding capabilities", "128k tokens", pricingInfo = "NVIDIA Cloud Credits"),
                ModelInfo("qwen/qwen2.5-72b-instruct", "Qwen 2.5 72B (NVIDIA)", ProviderType.NVIDIA_NIM, "NVIDIA NIM", "Top-tier open code & math model", "128k tokens", pricingInfo = "NVIDIA Cloud Credits")
            )
            ProviderType.GEMINI -> listOf(
                ModelInfo("gemini-3.5-flash", "Gemini 3.5 Flash", ProviderType.GEMINI, "Google Gemini", "Fast, versatile, ultra-long 1M token context", "1,048,576 tokens", supportsVision = true, supportsReasoning = true, pricingInfo = "Included with Gemini Key"),
                ModelInfo("gemini-3.1-pro-preview", "Gemini 3.1 Pro Preview", ProviderType.GEMINI, "Google Gemini", "Advanced reasoning, coding, & complex synthesis", "2,097,152 tokens", supportsVision = true, supportsReasoning = true, pricingInfo = "Included with Gemini Key"),
                ModelInfo("gemini-3.1-flash-lite-preview", "Gemini 3.1 Flash Lite", ProviderType.GEMINI, "Google Gemini", "Low latency lightweight model", "512k tokens", pricingInfo = "Included with Gemini Key"),
                ModelInfo("gemini-2.5-flash-image", "Gemini 2.5 Flash Image", ProviderType.GEMINI, "Google Gemini", "Multimodal text and high-res image generation", "128k tokens", supportsVision = true, pricingInfo = "Included with Gemini Key")
            )
            ProviderType.OPENAI -> listOf(
                ModelInfo("gpt-5", "GPT-5 flagship", ProviderType.OPENAI, "OpenAI", "Next-generation multimodal reasoning powerhouse", "256k tokens", supportsVision = true, supportsReasoning = true, pricingInfo = "$2.50 / 1M tokens"),
                ModelInfo("gpt-5-mini", "GPT-5 Mini", ProviderType.OPENAI, "OpenAI", "High speed lightweight AI assistant model", "128k tokens", supportsVision = true, pricingInfo = "$0.15 / 1M tokens"),
                ModelInfo("gpt-4o", "GPT-4o Omni", ProviderType.OPENAI, "OpenAI", "Flagship high intelligence model", "128k tokens", supportsVision = true, pricingInfo = "$5.00 / 1M tokens"),
                ModelInfo("o3-mini", "o3-mini Reasoning", ProviderType.OPENAI, "OpenAI", "STEM & coding reasoning model", "200k tokens", supportsReasoning = true, pricingInfo = "$1.10 / 1M tokens")
            )
            ProviderType.ANTHROPIC -> listOf(
                ModelInfo("claude-3-7-sonnet", "Claude 3.7 Sonnet (Hybrid)", ProviderType.ANTHROPIC, "Anthropic", "Hybrid reasoning & ultra-thoughtful response", "200k tokens", supportsVision = true, supportsReasoning = true, pricingInfo = "$3.00 / 1M tokens"),
                ModelInfo("claude-3-5-sonnet-20241022", "Claude 3.5 Sonnet", ProviderType.ANTHROPIC, "Anthropic", "Premier AI workspace & coding model", "200k tokens", supportsVision = true, pricingInfo = "$3.00 / 1M tokens"),
                ModelInfo("claude-3-5-haiku", "Claude 3.5 Haiku", ProviderType.ANTHROPIC, "Anthropic", "Lightning fast small model", "200k tokens", pricingInfo = "$0.80 / 1M tokens")
            )
            ProviderType.GROQ -> listOf(
                ModelInfo("llama-3.3-70b-versatile", "Llama 3.3 70B (Groq LPU)", ProviderType.GROQ, "Groq", "Sub-100ms response speed powered by Groq LPU", "128k tokens", pricingInfo = "$0.59 / 1M tokens"),
                ModelInfo("deepseek-r1-distill-llama-70b", "DeepSeek R1 Distill (Groq)", ProviderType.GROQ, "Groq", "Instant reasoning speed with Groq hardware", "128k tokens", supportsReasoning = true, pricingInfo = "$0.75 / 1M tokens"),
                ModelInfo("qwen-2.5-coder-32b", "Qwen 2.5 Coder 32B (Groq)", ProviderType.GROQ, "Groq", "High-speed code generation model", "32k tokens", pricingInfo = "$0.30 / 1M tokens")
            )
            ProviderType.OPENROUTER -> listOf(
                ModelInfo("openrouter/auto", "OpenRouter Auto Router", ProviderType.OPENROUTER, "OpenRouter", "Automatically routes to best price/performance", "200k tokens", supportsVision = true, supportsReasoning = true, pricingInfo = "Dynamic Routing"),
                ModelInfo("deepseek/deepseek-chat", "DeepSeek V3 (OpenRouter)", ProviderType.OPENROUTER, "OpenRouter", "Top open weights chat model", "64k tokens", pricingInfo = "$0.14 / 1M tokens"),
                ModelInfo("anthropic/claude-3.5-sonnet", "Claude 3.5 Sonnet (OpenRouter)", ProviderType.OPENROUTER, "OpenRouter", "Anthropic Sonnet via OpenRouter", "200k tokens", supportsVision = true, pricingInfo = "$3.00 / 1M tokens")
            )
            ProviderType.CUSTOM -> listOf(
                ModelInfo("custom-model-1", "Custom Endpoint Model", ProviderType.CUSTOM, "Custom API", "User-configured OpenAI-compatible endpoint", "128k tokens", pricingInfo = "Self-hosted / Custom")
            )
        }
    }

    // Stream generation flow
    fun generateChatStream(
        providerType: ProviderType,
        apiKey: String,
        baseUrl: String,
        modelId: String,
        systemInstruction: String,
        messages: List<Pair<String, String>>, // sender, text
        imageBase64: String? = null
    ): Flow<String> = flow {
        if (providerType == ProviderType.GEMINI) {
            emitAll(callGeminiStream(apiKey, modelId, systemInstruction, messages, imageBase64))
        } else {
            emitAll(callOpenAiCompatibleStream(providerType, apiKey, baseUrl, modelId, systemInstruction, messages))
        }
    }.flowOn(Dispatchers.IO)

    private fun callGeminiStream(
        apiKey: String,
        modelId: String,
        systemInstruction: String,
        messages: List<Pair<String, String>>,
        imageBase64: String? = null
    ): Flow<String> = flow {
        val targetModel = if (modelId.contains("gemini")) modelId else "gemini-3.5-flash"
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$targetModel:generateContent?key=$apiKey"

        val contentsArray = JSONArray()
        for ((sender, text) in messages) {
            val role = if (sender == "USER") "user" else "model"
            val contentObj = JSONObject()
            contentObj.put("role", role)

            val partsArray = JSONArray()
            val textPart = JSONObject()
            textPart.put("text", text)
            partsArray.put(textPart)

            if (role == "user" && imageBase64 != null && messages.last().second == text) {
                val imagePart = JSONObject()
                val inlineData = JSONObject()
                inlineData.put("mimeType", "image/jpeg")
                inlineData.put("data", imageBase64)
                imagePart.put("inlineData", inlineData)
                partsArray.put(imagePart)
            }

            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
        }

        val requestJson = JSONObject()
        requestJson.put("contents", contentsArray)

        if (systemInstruction.isNotBlank()) {
            val sysObj = JSONObject()
            val sysParts = JSONArray()
            val sysTextPart = JSONObject()
            sysTextPart.put("text", systemInstruction)
            sysParts.put(sysTextPart)
            sysObj.put("parts", sysParts)
            requestJson.put("systemInstruction", sysObj)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val responseStr = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                emit("Error (${response.code}): $responseStr")
                return@flow
            }

            val jsonResp = JSONObject(responseStr)
            val candidates = jsonResp.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val fullText = parts.getJSONObject(0).optString("text", "")
                    // Simulate streaming chunk delivery for smooth response rendering
                    val chunkSize = 12
                    var i = 0
                    while (i < fullText.length) {
                        val end = (i + chunkSize).coerceAtMost(fullText.length)
                        emit(fullText.substring(i, end))
                        i = end
                        kotlinx.coroutines.delay(18)
                    }
                } else {
                    emit("No response text returned.")
                }
            } else {
                emit("No candidate generated.")
            }
        } catch (e: Exception) {
            emit("API Connection Failure: ${e.localizedMessage}")
        }
    }

    private fun callOpenAiCompatibleStream(
        providerType: ProviderType,
        apiKey: String,
        customBaseUrl: String,
        modelId: String,
        systemInstruction: String,
        messages: List<Pair<String, String>>
    ): Flow<String> = flow {
        val endpoint = when (providerType) {
            ProviderType.NVIDIA_NIM -> "https://integrate.api.nvidia.com/v1/chat/completions"
            ProviderType.OPENAI -> "https://api.openai.com/v1/chat/completions"
            ProviderType.GROQ -> "https://api.groq.com/openai/v1/chat/completions"
            ProviderType.OPENROUTER -> "https://openrouter.ai/api/v1/chat/completions"
            ProviderType.ANTHROPIC -> "https://api.anthropic.com/v1/messages"
            ProviderType.CUSTOM -> if (customBaseUrl.isNotBlank()) customBaseUrl else "http://localhost:8080/v1/chat/completions"
            else -> "https://api.openai.com/v1/chat/completions"
        }

        val messagesArray = JSONArray()
        if (systemInstruction.isNotBlank()) {
            val sysObj = JSONObject()
            sysObj.put("role", "system")
            sysObj.put("content", systemInstruction)
            messagesArray.put(sysObj)
        }

        for ((sender, text) in messages) {
            val msgObj = JSONObject()
            msgObj.put("role", if (sender == "USER") "user" else "assistant")
            msgObj.put("content", text)
            messagesArray.put(msgObj)
        }

        val requestJson = JSONObject()
        requestJson.put("model", modelId)
        requestJson.put("messages", messagesArray)
        requestJson.put("temperature", 0.7)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)

        val reqBuilder = Request.Builder()
            .url(endpoint)
            .post(requestBody)

        if (apiKey.isNotBlank()) {
            reqBuilder.addHeader("Authorization", "Bearer $apiKey")
        }

        try {
            val response = okHttpClient.newCall(reqBuilder.build()).execute()
            val responseStr = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                emit("Provider Error (${response.code}): ${if (responseStr.length > 200) responseStr.take(200) + "..." else responseStr}")
                return@flow
            }

            val jsonResp = JSONObject(responseStr)
            val choices = jsonResp.optJSONArray("choices")
            if (choices != null && choices.length() > 0) {
                val choice = choices.getJSONObject(0)
                val msg = choice.optJSONObject("message")
                val text = msg?.optString("content", "") ?: ""
                
                var i = 0
                val chunkSize = 15
                while (i < text.length) {
                    val end = (i + chunkSize).coerceAtMost(text.length)
                    emit(text.substring(i, end))
                    i = end
                    kotlinx.coroutines.delay(18)
                }
            } else {
                emit("No choices received from provider endpoint.")
            }
        } catch (e: Exception) {
            emit("Network Exception: ${e.localizedMessage}")
        }
    }
}
