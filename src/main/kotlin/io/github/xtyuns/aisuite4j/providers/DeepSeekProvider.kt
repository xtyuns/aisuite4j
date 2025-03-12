package io.github.xtyuns.aisuite4j.providers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.xtyuns.aisuite4j.AiClient
import io.github.xtyuns.aisuite4j.ClientProvider
import io.github.xtyuns.aisuite4j.framework.ChatCompletionChunkResponse
import io.github.xtyuns.aisuite4j.framework.ChatCompletionResponse
import io.github.xtyuns.aisuite4j.framework.Message
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

class DeepSeekProvider : ClientProvider {
    override fun createClient(model: String, token: String, defaultOptions: Map<String, Any>): AiClient {
        return DeepSeekAiClient(token, defaultOptions + mapOf("model" to model))
    }

    override fun supportModels(): Set<String> {
        return setOf("deepseek-chat", "deepseek-reasoner")
    }
}

class DeepSeekAiClient(
    private val token: String,
    private val defaultOptions: Map<String, Any>
) : AiClient {
    private val objectMapper = ObjectMapper().also {
        it.registerKotlinModule()
        it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    private val webClient = WebClient.builder()
        .baseUrl("https://api.deepseek.com")
        .defaultHeader("Authorization", "Bearer $token")
        .build()

    override fun stream(messages: List<Message>, options: Map<String, Any>): Flux<ChatCompletionChunkResponse> {
        val bodyValue = defaultOptions + options + mapOf(
            "messages" to messages,
            "stream" to true
        )

        val chunkFlux = webClient.post().uri("/chat/completions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(bodyValue))
            .retrieve()
            .bodyToFlux(String::class.java)

        return chunkFlux.filter {
            it != "[DONE]"
        }.map {
            objectMapper.readValue(it, ChatCompletionChunkResponse::class.java)
        }
    }

    override fun call(messages: List<Message>, options: Map<String, Any>): ChatCompletionResponse {
        val bodyValue = defaultOptions + options + mapOf(
            "messages" to messages,
            "stream" to false
        )

        val mono = webClient.post().uri("/chat/completions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(bodyValue))
            .retrieve()
            .bodyToMono(String::class.java)

        return mono.map {
            objectMapper.readValue(it, ChatCompletionResponse::class.java)
        }.block()!!
    }
}